package dev.bastighg;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;

import java.util.*;

public class BastiMod implements ClientModInitializer {
    public static final String MODID = "bastighg";
    public static final int FLAG_TICKS = 500; // ~25 Sekunden Flagge oben

    public static BastiEntity basti;
    public static Country currentCountry;
    public static int flagUpTicks;

    private static final Random RNG = new Random();
    private static final SoundEvent TALK = SoundEvent.of(Identifier.of(MODID, "talk"));

    private static long tick;
    private static long nextCountryTick = -1;
    private static Country lastCountry;

    // Kisten
    private static final Set<BlockPos> placed = new HashSet<>();
    private static final Set<BlockPos> announced = new HashSet<>();
    private static BlockPos pendingChest;
    private static long pendingChestTick;
    private static int screenTicks;

    // Vault
    private static BlockPos vaultPos;
    private static long vaultTick, lastFound;
    private static final List<ItemEntity> vaultItems = new ArrayList<>();
    private static final Map<Integer, ItemStack> vaultFound = new LinkedHashMap<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(BastiMod::onTick);
        WorldRenderEvents.AFTER_ENTITIES.register(FlagRenderer::render);

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient) onUse(player, world, hit, player.getStackInHand(hand));
            return ActionResult.PASS;
        });

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity ie && vaultPos != null && tick - vaultTick < 200
                    && ie.getBlockPos().isWithinDistance(vaultPos, 5)) {
                vaultItems.add(ie);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            placed.clear();
            announced.clear();
            pendingChest = null;
            resetVault();
            basti = null;
            currentCountry = null;
            nextCountryTick = -1;
        });
    }

    // ---------------------------------------------------------------- Eingaben

    private static void onUse(PlayerEntity player, World world, BlockHitResult hit, ItemStack held) {
        BlockPos pos = hit.getBlockPos();
        // Vom Spieler platzierte Kisten merken -> das sind keine Struktur-Kisten
        if (held.isOf(Items.CHEST) || held.isOf(Items.TRAPPED_CHEST) || held.isOf(Items.BARREL)) {
            placed.add(pos.offset(hit.getSide()).toImmutable());
        }
        if (player.shouldCancelInteraction()) return;

        Block b = world.getBlockState(pos).getBlock();
        if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL) {
            pendingChest = pos.toImmutable();
            pendingChestTick = tick;
        } else if (b == Blocks.VAULT && (held.isOf(Items.TRIAL_KEY) || held.isOf(Items.OMINOUS_TRIAL_KEY))) {
            resetVault();
            vaultPos = pos.toImmutable();
            vaultTick = tick;
        }
    }

    // ---------------------------------------------------------------- Tick

    private static void onTick(MinecraftClient mc) {
        ClientWorld w = mc.world;
        ClientPlayerEntity p = mc.player;
        if (w == null || p == null) {
            basti = null;
            return;
        }
        tick++;

        if (basti == null || basti.isRemoved() || basti.getWorld() != w) {
            basti = new BastiEntity(w, p.getPos());
            w.addEntity(basti);
        }

        handleCountries();
        handleChest(mc, w);
        handleVault();
    }

    private static void handleCountries() {
        if (nextCountryTick < 0) nextCountryTick = tick + 400;

        if (currentCountry != null && ++flagUpTicks > FLAG_TICKS) currentCountry = null;

        if (tick >= nextCountryTick) {
            Country c;
            do {
                c = Countries.ALL.get(RNG.nextInt(Countries.ALL.size()));
            } while (c == lastCountry);
            lastCountry = c;
            currentCountry = c;
            flagUpTicks = 0;
            say(String.format(pick(COUNTRY_LINES), c.name(), c.capital()));
            nextCountryTick = tick + 2400 + RNG.nextInt(1201); // alle 2-3 Minuten
        }
    }

    private static void handleChest(MinecraftClient mc, ClientWorld w) {
        if (!(mc.currentScreen instanceof GenericContainerScreen gcs)) {
            screenTicks = 0;
            return;
        }
        if (pendingChest == null || tick - pendingChestTick > 200) return;
        if (++screenTicks < 8) return; // warten, bis der Server den Inhalt geschickt hat

        BlockPos pos = pendingChest;
        pendingChest = null;
        screenTicks = 0;

        Set<BlockPos> group = new HashSet<>();
        group.add(pos);
        BlockState st = w.getBlockState(pos);
        if (st.getBlock() instanceof ChestBlock && st.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
            group.add(pos.offset(ChestBlock.getFacing(st)).toImmutable());
        }
        if (!Collections.disjoint(group, placed) || !Collections.disjoint(group, announced)) return;
        announced.addAll(group);

        List<ItemStack> items = new ArrayList<>();
        int n = gcs.getScreenHandler().getRows() * 9;
        for (int i = 0; i < n; i++) {
            ItemStack s = gcs.getScreenHandler().slots.get(i).getStack();
            if (!s.isEmpty()) items.add(s.copy());
        }
        announceItems(items, false);
    }

    private static void handleVault() {
        if (vaultPos == null) return;
        for (ItemEntity ie : vaultItems) {
            ItemStack s = ie.getStack();
            if (!s.isEmpty() && !vaultFound.containsKey(ie.getId())) {
                vaultFound.put(ie.getId(), s.copy());
                lastFound = tick;
            }
        }
        if (!vaultFound.isEmpty() && tick - lastFound > 25) {
            announceItems(new ArrayList<>(vaultFound.values()), true);
            resetVault();
        } else if (tick - vaultTick > 300) {
            resetVault();
        }
    }

    private static void resetVault() {
        vaultPos = null;
        vaultItems.clear();
        vaultFound.clear();
    }

    // ---------------------------------------------------------------- Sprechen

    private static final String[] COUNTRY_LINES = {
            "Yo! Das Land ist %s, die Hauptstadt ist %s. Guck, ich hab die Flagge dabei!",
            "Zeit für Geographie: %s! Hauptstadt: %s. Merk dir das!",
            "Kleine Erdkunde-Runde: %s, Hauptstadt %s. Und hier die Flagge dazu!"
    };
    private static final String[] BEST_LINES = {
            "%s! Das ist hier drin wahrscheinlich das Beste.",
            "Nimm auf jeden Fall %s mit, das ist der Hauptgewinn!",
            "Ich wette, %s ist hier das Highlight."
    };
    private static final String[] VAULT_LINES = {
            "Vault geknackt! %s, das ist wahrscheinlich der Hauptgewinn.",
            "Boah, %s aus dem Vault! Nicht schlecht."
    };

    private static String pick(String[] a) {
        return a[RNG.nextInt(a.length)];
    }

    private static String label(ItemStack s) {
        return s.getCount() + "x " + s.getName().getString();
    }

    private static void announceItems(List<ItemStack> items, boolean vault) {
        if (items.isEmpty()) {
            say("Hier ist nix drin, Digga.");
            return;
        }
        items.sort(Comparator.<ItemStack>comparingInt(ItemRanker::score).reversed());
        say(String.format(pick(vault ? VAULT_LINES : BEST_LINES), label(items.get(0))));
        if (items.size() > 1) {
            StringBuilder sb = new StringBuilder("Top 3: ");
            for (int i = 0; i < Math.min(3, items.size()); i++) {
                if (i > 0) sb.append(", ");
                sb.append(label(items.get(i)));
            }
            MinecraftClient.getInstance().inGameHud.getChatHud()
                    .addMessage(Text.literal(sb.toString()).formatted(Formatting.GRAY));
        }
    }

    static void say(String msg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.getChatHud().addMessage(
                Text.literal("<BastiGHG> ").formatted(Formatting.GOLD)
                        .append(Text.literal(msg).formatted(Formatting.WHITE)));
        mc.getSoundManager().play(PositionedSoundInstance.master(TALK, 1.0f));
    }
}
