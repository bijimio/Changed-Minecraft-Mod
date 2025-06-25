package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ContentWarningScreen extends Screen {
    private static final ResourceLocation BACKGROUND_LOCATION = Changed.modResource("textures/gui/warning_background.png");
    private static final ResourceLocation ICON_LOCATION = Changed.modResource("textures/gui/warning_icon.png");
    private static final Component WARNING = Component.translatable("text.changed.warning");
    private static final Component WARNING_CONTENT1 = Component.translatable("text.changed.warning.content1");
    private static final Component WARNING_CONTENT2 = Component.translatable("text.changed.warning.content2");

    public ContentWarningScreen() {
        super(Component.literal("Content Warning"));
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(ICON_LOCATION, this.width / 2 - 45, 20, 0, 0, 90, 90, 90, 90);
    }

    @Override
    protected void init() {
        super.init();
        int l = this.height / 4 + 48 + 72;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.proceed"), (p_96776_) -> {
            Changed.config.client.showContentWarning.set(false);
            Changed.config.client.showContentWarning.save();
            this.minecraft.setScreen(new TitleScreen(true));
        }).bounds(this.width / 2 - 100, l, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, x, y, partialTick);

        final var pose = graphics.pose();

        pose.pushPose();
        pose.translate(this.width / 2.0, 130.0, 0.0);

        pose.pushPose();
        pose.scale(1.5f, 1.5f, 1.5f);
        graphics.drawCenteredString(this.font, WARNING, 0, -8, 0xFFFFFFFF);
        pose.popPose();

        pose.translate(0.0, 16.0, 0.0);
        graphics.drawCenteredString(this.font, WARNING_CONTENT1, 0, -8, 0xFFFFFFFF);
        pose.translate(0.0, 8.0, 0.0);
        graphics.drawCenteredString(this.font, WARNING_CONTENT2, 0, -8, 0xFFFFFFFF);
        pose.popPose();
    }
}
