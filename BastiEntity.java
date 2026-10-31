package dev.bastighg;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/** Reiner Client-Begleiter: existiert nur in der ClientWorld, der Server weiss nichts davon. */
public class BastiEntity extends OtherClientPlayerEntity {
    // Hohe ID, damit sie nie mit Server-Entity-IDs kollidiert
    private static final int ENTITY_ID = 0x7FFFFF00;
    private static final SkinTextures SKIN = new SkinTextures(
            Identifier.of(BastiMod.MODID, "textures/entity/basti.png"),
            null, null, null, SkinTextures.Model.WIDE, false);

    private double vy = 0;
    private int stuck = 0;

    public BastiEntity(ClientWorld world, Vec3d pos) {
        super(world, new GameProfile(UUID.nameUUIDFromBytes("BastiGHG".getBytes()), "BastiGHG"));
        setId(ENTITY_ID);
        refreshPositionAndAngles(pos.x, pos.y, pos.z, 0, 0);
    }

    @Override
    public SkinTextures getSkinTextures() {
        return SKIN;
    }

    @Override
    public boolean canHit() {
        return false; // nicht anklickbar/angreifbar -> keine Pakete an den Server
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) return;

        Vec3d to = p.getPos().subtract(getPos());
        double dist = Math.hypot(to.x, to.z);
        if (to.lengthSquared() > 40 * 40 || (stuck > 120 && dist > 8)) {
            teleportNear(p);
            return;
        }

        double speed = 0;
        if (dist > 3.2) speed = Math.min(dist > 10 ? 0.45 : 0.30, 0.10 + (dist - 3.2) * 0.05);
        double hx = 0, hz = 0;
        if (dist > 1e-4 && speed > 0) {
            hx = to.x / dist * speed;
            hz = to.z / dist * speed;
        }

        float target = (float) (MathHelper.atan2(to.z, to.x) * 180.0 / Math.PI) - 90f;
        float yaw = MathHelper.lerpAngleDegrees(0.35f, getYaw(), target);
        setYaw(yaw);
        this.bodyYaw = yaw;
        this.headYaw = yaw;
        setPitch((float) (-Math.atan2(p.getEyeY() - getEyeY(), Math.max(dist, 0.1)) * 180.0 / Math.PI));

        if (isTouchingWater() || isInLava()) vy = 0.12;
        else vy = (vy - 0.08) * 0.98;

        Vec3d before = getPos();
        move(MovementType.SELF, new Vec3d(hx, vy, hz));
        if (verticalCollision) vy = vy < 0 ? -0.0784 : 0;
        if (horizontalCollision && isOnGround() && speed > 0) vy = 0.42; // ueber Bloecke huepfen
        stuck = (speed > 0 && getPos().distanceTo(before) < 0.02) ? stuck + 1 : 0;
    }

    private void teleportNear(ClientPlayerEntity p) {
        Vec3d back = p.getRotationVec(1f);
        refreshPositionAndAngles(p.getX() - back.x * 2, p.getY(), p.getZ() - back.z * 2, p.getYaw(), 0);
        vy = 0;
        stuck = 0;
    }
}
