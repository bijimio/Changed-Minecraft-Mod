package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorSelector extends EditBox {
    private static final int padding = 5;
    final Supplier<Color3> colorGetter;
    final Consumer<Color3> colorSetter;
    private final Component name;
    private final FormattedCharSequence nameCharSequence;
    private final int realWidth;
    private boolean lastHovered = false;

    private static final int COLOR_GOOD = 14737632;
    private static final int COLOR_ERROR = 16733525;

    public ColorSelector(Font font, int x, int y, int width, int height, Component name, Supplier<Color3> colorGetter, Consumer<Color3> colorSetter) {
        super(font, x, y, width - height - padding, height, name);
        this.name = name;
        this.nameCharSequence = name.getVisualOrderText();
        this.colorGetter = colorGetter;
        this.colorSetter = colorSetter;
        this.realWidth = width;

        this.insertText(colorGetter.get().toHexCode());
        this.setResponder(this::onValueChange);
        this.setFilter(this::validColor);
        this.setFormatter(this::onFormat);
        this.setTooltip(Tooltip.create(Component.translatable("changed.config.color_picker_tooltip")));
    }

    public Component getName() {
        return name;
    }

    private FormattedCharSequence onFormat(String text, int i) {
        if (this.isHoveredOrFocused())
            return FormattedCharSequence.forward(text, Style.EMPTY);
        else
            return nameCharSequence;
    }

    private void onValueChange(String text) {
        try {
            var color = Color3.parseHex(text);
            if (color != null) {
                colorSetter.accept(color);
                this.setTextColor(COLOR_GOOD);
            }

            else {
                this.setTextColor(COLOR_ERROR);
            }
        } catch (Exception ex) {
            this.setTextColor(COLOR_ERROR);
        }
    }

    private boolean validColor(String text) {
        if (text.isEmpty())
            return true;
        if (text.length() == 1 && text.charAt(0) == '#')
            return true;
        return Color3.parseHex(text) != null;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTime) {
        super.renderWidget(graphics, mouseX, mouseY, deltaTime);

        if (this.isVisible()) {
            // Render color preview
            int startX = this.getX() + this.width + padding;
            int startY = this.getY();
            int endX = startX + this.height;
            int endY = startY + this.height;

            graphics.fill(startX - 1, startY - 1, endX + 1, endY + 1, 0);

            var color = colorGetter.get();
            graphics.fill(startX, startY, endX, endY, color.toInt());
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            return true;

        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.clicked(x, y);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(x, y);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    protected boolean clicked(double x, double y) {
        return this.active && this.visible && x >= (double)this.getX() && y >= (double)this.getY() && x < (double)(this.getX() + this.realWidth) && y < (double)(this.getY() + this.height);
    }

    @Override
    public void onClick(double x, double y) {
        super.onClick(x, y);

        if (this.isVisible()) {
            int startX = this.getX() + this.width + padding;
            int startY = this.getY();
            int endX = startX + this.height;
            int endY = startY + this.height;

            if (x >= startX && x < endX && y >= startY && y < endY) {
                Minecraft.getInstance().setScreen(new AdvancedColorSelectorScreen(Minecraft.getInstance().screen, this));
            }
        }
    }
}
