package lehjr.numina.integration.scannable;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.util.Migration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslatableComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Copy of Scannable's overlay renderer recreated here because the item check below
 */
public enum MPSOverlayRenderer {
    INSTANCE;

    private static final ResourceLocation PROGRESS = new ResourceLocation("scannable", "textures/gui/overlay/scanner_progress.png");

    @SubscribeEvent
    public void onOverlayRender(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (player == null) {
            return;
        }

        final ItemStack stack = player.getUseItem();
        if (stack.isEmpty()) {
            return;
        }

        if (!ScannableHandler.isScanner(stack)) {
            return;
        }

        final int total = stack.getUseDuration();
        final int remaining = player.getUseItemRemainingTicks();

        final float progress = MathHelper.clamp(1 - (remaining - event.getPartialTicks()) / (float) total, 0, 1);

        final int screenWidth = mc.getWindow().getGuiScaledWidth();
        final int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.color4f(0.66f, 0.8f, 0.93f, 0.66f);
        mc.getTextureManager().bind(PROGRESS);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

        final int width = 64;
        final int height = 64;
        final int midX = screenWidth / 2;
        final int midY = screenHeight / 2;
        final int left = midX - width / 2;
        final int right = midX + width / 2;
        final int top = midY - height / 2;
        final int bottom = midY + height / 2;

        final float angle = (float) (progress * Math.PI * 2);
        final float tx = MathHelper.sin(angle);
        final float ty = MathHelper.cos(angle);

        buffer.vertex(midX, top, 0).uv(0.5f, 1).endVertex();
        if (progress < 0.125) { // Top right.
            buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

            final float x = tx / ty * 0.5f;
            buffer.vertex(midX + x * width, top, 0).uv(0.5f + x, 1).endVertex();
        } else {
            buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
            buffer.vertex(right, top, 0).uv(1, 1).endVertex();

            buffer.vertex(right, top, 0).uv(1, 1).endVertex();
            if (progress < 0.375) { // Right.
                buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                final float y = Math.abs(ty / tx - 1) * 0.5f;
                buffer.vertex(right, top + y * height, 0).uv(1, 1 - y).endVertex();
            } else {
                buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                buffer.vertex(right, bottom, 0).uv(1, 0).endVertex();

                buffer.vertex(right, bottom, 0).uv(1, 0).endVertex();
                if (progress < 0.625) { // Bottom.
                    buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                    final float x = Math.abs(tx / ty - 1) * 0.5f;
                    buffer.vertex(left + x * width, bottom, 0).uv(x, 0).endVertex();
                } else {
                    buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                    buffer.vertex(left, bottom, 0).uv(0, 0).endVertex();

                    buffer.vertex(left, bottom, 0).uv(0, 0).endVertex();
                    if (progress < 0.875) { // Left.
                        buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                        final float y = (ty / tx + 1) * 0.5f;
                        buffer.vertex(left, top + y * height, 0).uv(0, 1 - y).endVertex();
                    } else {
                        buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                        buffer.vertex(left, top, 0).uv(0, 1).endVertex();

                        buffer.vertex(left, top, 0).uv(0, 1).endVertex();
                        if (progress < 1) { // Top left.
                            buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                            final float x = Math.abs(tx / ty) * 0.5f;
                            buffer.vertex(midX - x * width, top, 0).uv(0.5f - x, 1).endVertex();
                        } else {
                            buffer.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                            buffer.vertex(midX, top, 0).uv(0.5f, 1).endVertex();
                        }
                    }
                }
            }
        }

        tessellator.end();

        Migration.FontRenderer.drawStringWithShadow(mc.font, event.getPoseStack(),
                new TranslatableComponent(Constants.GUI_SCANNER_PROGRESS, MathHelper.floor(progress * 100)),
                right + 12, midY - mc.font.lineHeight / 2, 0xCCAACCEE);

        RenderSystem.bindTexture(0);
    }
}
