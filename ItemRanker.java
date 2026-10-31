package dev.bastighg;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

/** Bewertet Items grob nach Wert, damit Basti das "beste" Item nennen kann. */
public final class ItemRanker {
    private static final Map<Item, Integer> VALUE = new HashMap<>();

    private static void put(int v, Item... items) {
        for (Item i : items) VALUE.put(i, v);
    }

    static {
        put(100, Items.ELYTRA, Items.NETHER_STAR);
        put(98, Items.MACE);
        put(97, Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL,
                Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);
        put(95, Items.ENCHANTED_GOLDEN_APPLE, Items.HEAVY_CORE);
        put(92, Items.NETHERITE_INGOT);
        put(90, Items.TOTEM_OF_UNDYING);
        put(88, Items.TRIDENT, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        put(85, Items.HEART_OF_THE_SEA);
        put(80, Items.DIAMOND_BLOCK, Items.ANCIENT_DEBRIS);
        put(78, Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL,
                Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
        put(75, Items.DIAMOND_HORSE_ARMOR);
        put(70, Items.DIAMOND);
        put(60, Items.OMINOUS_TRIAL_KEY, Items.ECHO_SHARD, Items.EMERALD_BLOCK);
        put(55, Items.GOLDEN_APPLE, Items.SADDLE);
        put(45, Items.DISC_FRAGMENT_5, Items.GOLD_BLOCK, Items.IRON_BLOCK);
        put(40, Items.NAME_TAG, Items.EMERALD);
        put(28, Items.GOLD_INGOT);
        put(25, Items.IRON_INGOT, Items.LAPIS_LAZULI);
        put(20, Items.GOLDEN_CARROT);
    }

    public static int score(ItemStack s) {
        int v = VALUE.getOrDefault(s.getItem(), 0);
        if (v == 0) {
            v = switch (s.getRarity()) {
                case EPIC -> 60;
                case RARE -> 45;
                case UNCOMMON -> 25;
                default -> 8;
            };
        }
        ItemEnchantmentsComponent stored = s.get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (stored != null && !stored.isEmpty()) v = Math.max(v, 45) + 8 * stored.getSize();
        if (s.hasEnchantments()) v += 25;
        return v * 100 + Math.min(s.getCount(), 64);
    }
}
