package net.slayer.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import net.slayer.SanguinareClient;
import net.slayer.SanguinareMain;

public class BloodBar implements HudRenderCallback {
    private static final Identifier FILLED_BLOOD = new Identifier(SanguinareMain.MOD_ID,
            "textures/blood/filled_blood.png");
    private static final Identifier EMPTY_BLOOD = new Identifier(SanguinareMain.MOD_ID,
            "textures/blood/empty_blood.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        int x = 0;
        int y = 0;

        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            x = width / 2;
            y = height;
        }

        if (SanguinareClient.getSanguinareBooleans("sanguinareStatus")) {

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, EMPTY_BLOOD);
            for (int i = 0; i < 10; i++) {
                drawContext.drawTexture(EMPTY_BLOOD, x + 10 + (i * 8), y - 40, 0, 0, 10, 10,
                        10, 10);
            }

            RenderSystem.setShaderTexture(0, FILLED_BLOOD);
            for (int i = 0; i < 10; i++) {
                if (SanguinareClient.getSanguinareInts("blood") > i) {
                    drawContext.drawTexture(FILLED_BLOOD, x + 20 + (i * 9), y - 70, 0, 0, 9, 9,
                            12, 12);
                } else {
                    break;
                }
            }
        }
    }
}
