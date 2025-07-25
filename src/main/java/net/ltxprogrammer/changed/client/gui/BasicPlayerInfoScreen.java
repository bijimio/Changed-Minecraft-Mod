package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class BasicPlayerInfoScreen extends Screen {
    private final Screen lastScreen;
    private final @Nullable Player player;
    private @Nullable Runnable toolTip = null;

    public BasicPlayerInfoScreen(Screen parent) {
        super(Component.translatable("changed.config.bpi.screen"));
        this.lastScreen = parent;
        this.player = null;
    }

    public BasicPlayerInfoScreen(Screen parent, Player player) {
        super(Component.translatable("changed.config.bpi.screen"));
        this.lastScreen = parent;
        this.player = player;
    }

    public void setToolTip(Runnable fn) {
        this.toolTip = fn;
    }

    @Override
    public void removed() {
        Changed.config.saveAdditionalData();
        if (this.player != null)
            Changed.PACKET_HANDLER.sendToServer(BasicPlayerInfoPacket.Builder.of(this.player));
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        super.init();
        var bpi = Changed.config.client.basicPlayerInfo;
        int i = 0;

        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.hair_color"),
                bpi::getHairColor, bpi::setHairColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.sclera_color"),
                bpi::getScleraColor, bpi::setScleraColor));
        i++;
        var rightIris = this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.iris_color.right"),
                bpi::getRightIrisColor, bpi::setRightIrisColor));
        i++;
        var leftIris = this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.iris_color.left"),
                bpi::getLeftIrisColor, bpi::setLeftIrisColor));
        this.addRenderableWidget(Button.builder(Component.translatable("changed.config.bpi.iris_color.sync"), button -> {
                    leftIris.setValue(rightIris.getValue());
                    //bpi.setLeftIrisColor(bpi.getRightIrisColor());
                }).bounds((this.width / 2 - 155 + i % 2 * 160) + 110, (this.height / 6 + 24 * (i >> 1)) + 24, 40, 20)
                .tooltip(Tooltip.create(Component.translatable("changed.config.bpi.iris_color.sync_tooltip"))).build());
        i++;
        this.addRenderableWidget(Button.builder(Component.translatable("changed.config.bpi.eye_style", bpi.getEyeStyle().getName()), button -> {
            var style = bpi.getEyeStyle();
            int id = style.ordinal();
            if (id < EyeStyle.values().length - 1)
                id += 1;
            else
                id = 0;
            style = EyeStyle.values()[id];
            bpi.setEyeStyle(style);
            button.setMessage(Component.translatable("changed.config.bpi.eye_style", style.getName()));
        }).bounds(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20).build());
        i += 2;
        this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.size"), bpi.getSizeValueForConfiguration()) {
            {
                this.updateMessage();
            }

            private double convertToScaledValue() {
                return (this.value * BasicPlayerInfo.getSizeTolerance() * 2) - BasicPlayerInfo.getSizeTolerance() + 1.0;
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("changed.config.bpi.size.value", Math.round(convertToScaledValue() * 100)));
            }

            @Override
            protected void applyValue() {
                bpi.setSize((float)convertToScaledValue());
            }
        });
        i += 2;
        this.addRenderableWidget(new Checkbox(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.override_dl_iris"), bpi.isOverrideIrisOnDarkLatex()) {
            @Override
            public void onPress() {
                super.onPress();
                bpi.setOverrideIrisOnDarkLatex(this.selected());
            }
        });
        i += 2;
        this.addRenderableWidget(new Checkbox(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Component.translatable("changed.config.bpi.override_all_eye_styles"), bpi.isOverrideOthersToMatchStyle()) {
            @Override
            public void onPress() {
                super.onPress();
                bpi.setOverrideOthersToMatchStyle(this.selected());
            }
        });
        i += 2;

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_96700_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(graphics, p_96563_, p_96564_, p_96565_);
        if (toolTip != null) {
            toolTip.run();
            toolTip = null;
        }
    }
}
