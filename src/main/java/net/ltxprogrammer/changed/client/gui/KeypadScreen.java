package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.world.inventory.KeypadMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Range;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeypadScreen extends AbstractContainerScreen<KeypadMenu> {
    private static final ResourceLocation BACKGROUND = Changed.modResource("textures/gui/keypad.png");
    private static final ResourceLocation NUMERALS = Changed.modResource("textures/gui/numerals.png");

    private final List<Byte> attemptedCode = new ArrayList<>();
    private final Player player;
    public KeypadScreen(KeypadMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 233;
        this.imageHeight = 61;
        this.player = inventory.player;
    }

    private void renderNumeral(GuiGraphics graphics, @Range(from = 0, to = 10) byte numeral, int x, int y) {
        if (numeral == 10 && player.tickCount % 10 >= 5)
            return; // Blinking cursor

        int numX;
        int numY;
        if (numeral >= 1 && numeral < 8) {
            numX = 22 * (numeral - 1);
            numY = 0;
        } else if (numeral >= 8 && numeral < 10) {
            numX = 22 * (numeral - 8);
            numY = 30;
        } else if (numeral == 0) {
            numX = 44;
            numY = 30;
        } else {
            numX = 66;
            numY = 30;
        }
        graphics.blit(NUMERALS, x, y, numX, numY, 22, 30, 158, 64);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        this.renderBackground(graphics);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        for (int idx = 0; idx < 8; ++idx) {
            int numX = this.leftPos + 8 + (idx * 28);
            int numY = this.topPos + 22;

            if (idx < attemptedCode.size()) {
                renderNumeral(graphics, attemptedCode.get(idx), numX, numY);
            } else if (idx == attemptedCode.size()) {
                renderNumeral(graphics, (byte)10, numX, numY);
            } else break;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int x, int y) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            menu.useCode(attemptedCode);
            player.closeContainer();
            return true;
        }

        else if (!attemptedCode.isEmpty() && key == GLFW.GLFW_KEY_BACKSPACE) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ChangedSounds.SWITCH1.get(), 1.0F));
            attemptedCode.remove(attemptedCode.size() - 1);
            return true;
        }

        if (attemptedCode.size() >= 8)
            return super.keyPressed(key, b, c);

        if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ChangedSounds.SWITCH1.get(), 1.0F));
            attemptedCode.add((byte)(key - GLFW.GLFW_KEY_0));
            return true;
        }

        else if (key >= GLFW.GLFW_KEY_KP_0 && key <= GLFW.GLFW_KEY_KP_9) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ChangedSounds.SWITCH1.get(), 1.0F));
            attemptedCode.add((byte)(key - GLFW.GLFW_KEY_KP_0));
            return true;
        }

        return super.keyPressed(key, b, c);
    }
}
