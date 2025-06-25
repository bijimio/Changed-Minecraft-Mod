package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AdvancedColorSelectorScreen extends Screen {
    private final Screen lastScreen;
    private final ColorSelector field;
    private static final ResourceLocation GRADIENT = Changed.modResource("textures/gui/gradient.png");

    public AdvancedColorSelectorScreen(Screen parent, ColorSelector field) {
        super(field.getName());
        this.lastScreen = parent;
        this.field = field;
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        super.init();
        int i = 0;

        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + (i % 2 * 160) + 80, this.height / 6 + 24 * (i >> 1), 150, 20, field.getName(), field.colorGetter, field.colorSetter));
        i += 2;
        this.addRenderableWidget(createRGBSlider(this.width / 2 - 155 + (i % 2 * 160), this.height / 6 + 24 * (i >> 1), 310, 20, Color3.parseHex("#FF0000"), "changed.config.color_picker_red"));
        i += 2;
        this.addRenderableWidget(createRGBSlider(this.width / 2 - 155 + (i % 2 * 160), this.height / 6 + 24 * (i >> 1), 310, 20, Color3.parseHex("#00FF00"), "changed.config.color_picker_green"));
        i += 2;
        this.addRenderableWidget(createRGBSlider(this.width / 2 - 155 + (i % 2 * 160), this.height / 6 + 24 * (i >> 1), 310, 20, Color3.parseHex("#0000FF"), "changed.config.color_picker_blue"));
        i += 2;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int p_96563_, int p_96564_, float partialTicks) {
        this.renderDirtBackground(graphics);
        super.render(graphics, p_96563_, p_96564_, partialTicks);
    }

    private AbstractWidget createRGBSlider(int x, int y, int width, int height, Color3 color, String translationKey) {
        return new AbstractSliderButton(x, y, width, height, Component.empty(), field.colorGetter.get().dot(color)) {
            {
                this.updateMessage();
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int x, int y, float partialTicks) {
                var baseColor = field.colorGetter.get();
                float r = baseColor.red() * (1.0f - color.red());
                float g = baseColor.green() * (1.0f - color.green());
                float b = baseColor.blue() * (1.0f - color.blue());

                RenderSystem.setShaderTexture(0, GRADIENT);
                RenderSystem.setShaderColor(r, g, b, 1.0F);
                graphics.blit(GRADIENT, this.getX(), this.getY(), 0, 20, 310, 20, 310,40);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), 1.0F);
                graphics.blit(GRADIENT, this.getX(), this.getY(), 0, 0, 310, 20, 310,40);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                super.renderWidget(graphics, x, y, partialTicks);
            }

            protected void updateMessage() {
                this.setMessage(Component.translatable(translationKey, field.colorGetter.get().dot(color)));
            }

            protected void applyValue() {
                var baseColor = field.colorGetter.get();
                float r = baseColor.red() * (1.0f - color.red()) + (float)this.value * color.red();
                float g = baseColor.green() * (1.0f - color.green()) + (float)this.value * color.green();
                float b = baseColor.blue() * (1.0f - color.blue()) + (float)this.value * color.blue();
                field.colorSetter.accept(new Color3(r, g, b));
            }
        };
    }
}
