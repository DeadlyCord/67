package dev.bastighg;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Zeichnet die Flagge samt Mast als Billboard ueber Bastis Kopf. */
public final class FlagRenderer {
    private static final Identifier ID = Identifier.of(BastiMod.MODID, "flag_dynamic");
    private static final float PX = 0.06f; // Blockgroesse eines Flaggen-Pixels
    private static NativeImageBackedTexture tex;
    private static Country paintedFor;

    public static void render(WorldRenderContext ctx) {
        BastiEntity b = BastiMod.basti;
        Country c = BastiMod.currentCountry;
        MatrixStack m = ctx.matrixStack();
        if (b == null || c == null || m == null || ctx.consumers() == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (tex == null) {
            tex = new NativeImageBackedTexture(new NativeImage(NativeImage.Format.RGBA,
                    FlagPainter.TEX_W, FlagPainter.TEX_H, true));
            mc.getTextureManager().registerTexture(ID, tex);
        }
        if (paintedFor != c) {
            FlagPainter.paint(tex.getImage(), c.flag());
            tex.upload();
            paintedFor = c;
        }

        // Hochziehen am Anfang, Runterlassen am Ende
        int up = BastiMod.flagUpTicks;
        float raise = Math.min(1f, Math.min(up / 20f, (BastiMod.FLAG_TICKS - up) / 20f));
        if (raise <= 0) return;

        float delta = ctx.tickCounter().getTickDelta(false);
        Vec3d p = b.getLerpedPos(delta);
        Vec3d cam = ctx.camera().getPos();

        m.push();
        m.translate(p.x - cam.x, p.y - cam.y + b.getHeight() + 0.1, p.z - cam.z);
        m.multiply(ctx.camera().getRotation()); // Billboard zur Kamera
        m.scale(1f, raise, 1f);
        Matrix4f mat = m.peek().getPositionMatrix();

        VertexConsumer vc = ctx.consumers().getBuffer(RenderLayer.getEntityCutoutNoCull(ID));
        float w = FlagPainter.TEX_W * PX, h = FlagPainter.TEX_H * PX, x0 = -PX;
        vertex(vc, mat, x0, 0, 0, 1);
        vertex(vc, mat, x0 + w, 0, 1, 1);
        vertex(vc, mat, x0 + w, h, 1, 0);
        vertex(vc, mat, x0, h, 0, 0);
        m.pop();
    }

    private static void vertex(VertexConsumer vc, Matrix4f mat, float x, float y, float u, float v) {
        vc.vertex(mat, x, y, 0f)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(0f, 1f, 0f);
    }
}
