package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StasisChamberScreen extends AbstractContainerScreen<StasisChamberMenu> {
    private final StasisChamberMenu menu;
    private @Nullable Runnable toolTip = null;

    public StasisChamberScreen(StasisChamberMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.menu = menu;
        this.imageWidth = 285;
        this.imageHeight = 166;

        menu.requestUpdate();
    }

    public void setToolTip(Runnable fn) {
        this.toolTip = fn;
    }

    private static final ResourceLocation texture = Changed.modResource("textures/gui/stasis_chamber.png");
    private static final ResourceLocation GENDER_SWITCH_LOCATION = Changed.modResource("textures/gui/gender_switch.png");

    private ButtonScope buttonScope = ButtonScope.DEFAULT;
    private ModifyType modifyType = ModifyType.DEFAULT;

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.menu.hideSlots = buttonScope != ButtonScope.DEFAULT;

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        final int textLeftMargin = this.leftPos + 176;
        final int textRightMargin = this.leftPos + 278;
        final int textTopMargin = this.topPos + 6;
        final int commandsTopMargin = this.topPos + 24;
        final int commandsBottomMargin = this.topPos + 136;

        if (buttonScope == ButtonScope.PROGRAMS) {
            menu.getChamberedEntity().ifPresent(entity -> {
                graphics.drawCenteredString(this.font, entity.getDisplayName(), (textLeftMargin + textRightMargin) / 2, textTopMargin, 0xFFFFFF);
            });

            AtomicInteger yOffset = new AtomicInteger(0);
            menu.getCurrentCommand().ifPresent(command -> {
                graphics.drawString(this.font, command.getActiveDisplayText(), textLeftMargin, commandsTopMargin + yOffset.getAndAdd(12), 0x20FF20);
            });
            menu.getScheduledCommands().forEach(command -> {
                graphics.drawString(this.font, command.getDisplayText(), textLeftMargin, commandsTopMargin + yOffset.getAndAdd(12), 0xFFFFFF);
            });
        }

        else {
            menu.getChamberedEntity().ifPresent(entity -> {
                graphics.drawCenteredString(this.font, entity.getDisplayName(), (textLeftMargin + textRightMargin) / 2, textTopMargin, 0xFFFFFF);

                int entityX = (textLeftMargin + textRightMargin) / 2;
                int entityY = commandsBottomMargin - 5;
                InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, entityX, entityY, 40,
                        (float)(this.leftPos) - mouseX + entityX,
                        (float)(this.topPos) - mouseY + (entityY - 100),
                        entity);
            });
        }

        this.renderTooltip(graphics, mouseX, mouseY);

        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            if (menu.getCustomSlot(0) == this.hoveredSlot) {
                setToolTip(() -> graphics.renderTooltip(this.font, Component.translatable("changed.stasis.slot.variant.tooltip"), mouseX, mouseY));
            }

            else if (menu.getCustomSlot(1) == this.hoveredSlot) {
                setToolTip(() -> graphics.renderTooltip(this.font, Component.translatable("changed.stasis.slot.fluid.tooltip"), mouseX, mouseY));
            }
        }


        if (toolTip != null) {
            if (this.menu.getCarried().isEmpty()) {
                toolTip.run();
            }
            toolTip = null;
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth + 18, this.imageHeight);

        this.menu.slots.forEach(slot -> {
            if (slot.isActive()) {
                graphics.blit(texture, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, this.imageWidth, 0, 18, 18, this.imageWidth + 18, this.imageHeight);
            }
        });

        graphics.blit(Changed.modResource("textures/gui/progress_bar_back.png"),
                this.leftPos + 217, this.topPos + 144, 0, 0, 48, 12, 48, 12);

        float progress = menu.getFluidLevel(partialTicks);
        graphics.blit(Changed.modResource("textures/gui/progress_bar_front.png"),
                this.leftPos + 217, this.topPos + 144, 0, 0, (int)(48 * progress), 12, 48, 12);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(key, b, c);
    }

    public enum ButtonScope {
        DEFAULT,
        PROGRAMS,
        MODIFICATIONS;

        public ButtonScope toggleScope(ButtonScope intendedScope) {
            if (this == DEFAULT)
                return intendedScope;
            if (this == intendedScope)
                return DEFAULT;

            return intendedScope;
        }
    }

    public enum ModifyType {
        DEFAULT,
        GENDERED,
        CUSTOM
    }

    private boolean initialized = false;
    private Button programsButton;
    private Button modifyButton;
    private Button openDoorButton;
    private Button closeDoorButton;

    private List<AbstractWidget> programWidgets = new ArrayList<>();
    private AbstractSliderButton waitDurationSlider;
    private List<AbstractWidget> modificationWidgets = new ArrayList<>();
    private Button genderButton;

    private void checkButtonStates() {
        if (!initialized) return;

        boolean open = menu.isChamberOpen();
        float fillLevel = menu.getChamberFillLevel(Minecraft.getInstance().getFrameTime());
        var fillFluid = menu.getChamberFillFluid();
        var chamberedEntity = menu.getChamberedEntity();

        programsButton.active = true;
        modifyButton.active = true;
        openDoorButton.active = !open && fillLevel <= 0f;
        openDoorButton.visible = buttonScope == ButtonScope.DEFAULT;
        closeDoorButton.active = open;
        closeDoorButton.visible = buttonScope == ButtonScope.DEFAULT;

        modifyType = menu.getChamberedLatex().map(entity -> {
            if (entity.getChangedEntity() instanceof CustomLatexEntity)
                return ModifyType.CUSTOM;
            else if (ChangedTransfurVariants.Gendered.getOpposite(entity.getSelfVariant()).isPresent())
                return ModifyType.GENDERED;
            return ModifyType.DEFAULT;
        }).orElse(ModifyType.CUSTOM); // Default to custom to allow pre-programming

        programWidgets.forEach(button -> button.visible = buttonScope == ButtonScope.PROGRAMS);
        genderButton.visible = buttonScope == ButtonScope.MODIFICATIONS && modifyType == ModifyType.GENDERED;
        modificationWidgets.forEach(button -> button.visible = buttonScope == ButtonScope.MODIFICATIONS && modifyType == ModifyType.CUSTOM);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        checkButtonStates();
    }

    @Override
    public void init() {
        super.init();

        int leftMargin = this.leftPos + 7;
        int topMargin = this.topPos + 7;
        int buttonWidth = 79;
        int buttonHeight = 20;
        int buttonHeightSpacing = buttonHeight + 4;
        int buttonWidthSpacing = buttonWidth + 4;
        int buttonWidthLong = buttonWidth + buttonWidthSpacing;

        programsButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.programs"), button -> {
            buttonScope = buttonScope.toggleScope(ButtonScope.PROGRAMS);
            this.checkButtonStates();
        }).bounds(leftMargin, topMargin, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.programs.tooltip"))).build());

        modifyButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify"), button -> {
            buttonScope = buttonScope.toggleScope(ButtonScope.MODIFICATIONS);
            this.checkButtonStates();
        }).bounds(leftMargin + buttonWidthSpacing, topMargin, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.tooltip"))).build());

        openDoorButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.open"), button -> {
            menu.sendSimpleCommand(StasisChamberMenu.Command.OPEN_DOOR);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.open.tooltip"))).build());

        closeDoorButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.close"), button -> {
            menu.sendSimpleCommand(StasisChamberMenu.Command.CLOSE_DOOR);
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.close.tooltip"))).build());

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.transfur"), button -> {
            menu.inputProgram("transfur");
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.transfur.tooltip"))).build()));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.modify"), button -> {
            menu.inputProgram("modify");
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.modify.tooltip"))).build()));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.capture_next"), button -> {
            menu.inputProgram("captureNextEntity");
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 2, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.capture_next.tooltip"))).build()));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.toggle_stasis"), button -> {
            menu.inputProgram("toggleStasis");
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing * 2, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.toggle_stasis.tooltip"))).build()));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.create_entity"), button -> {
            menu.inputProgram("createEntity");
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 3, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.create_entity.tooltip"))).build()));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.discard_entity"), button -> {
            menu.inputProgram("discardEntity");
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing * 3, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.discard_entity.tooltip"))).build()));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.abort"), button -> {
            menu.inputProgram("abort");
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 4, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.abort.tooltip"))).build()));

        waitDurationSlider = this.addRenderableWidget(new AbstractSliderButton(leftMargin, topMargin + buttonHeightSpacing * 5, buttonWidthLong, buttonHeight, Component.translatable("changed.stasis.program.wait_duration", menu.getWaitDurationSeconds(menu.getWaitDurationPercent())), menu.getWaitDurationPercent()) {
            @Override
            protected void updateMessage() {
                setMessage(Component.translatable("changed.stasis.program.wait_duration", menu.getWaitDurationSeconds(this.value)));
            }

            @Override
            protected void applyValue() {
                menu.inputWaitDuration(this.value);
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int p_93677_, int p_93678_, float p_93679_) {
                if (!(this.isHoveredOrFocused() && minecraft.mouseHandler.isLeftPressed())) {
                    double last = this.value;
                    this.value = menu.getWaitDurationPercent();
                    if (last != this.value)
                        updateMessage();
                }
                super.renderWidget(graphics, p_93677_, p_93678_, p_93679_);
            }
        });
        programWidgets.add(waitDurationSlider);

        genderButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.gender"), button -> {
            menu.inputModifyConfig(menu.getConfiguredCustomLatex());
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidthLong, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.gender.tooltip"))).build());

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.torso", CustomLatexEntity.TorsoType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.TorsoType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.torso", next.name()));
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.torso.tooltip"))).build()));

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.hair", CustomLatexEntity.HairType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.HairType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.hair", next.name()));
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.hair.tooltip"))).build()));

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.ears", CustomLatexEntity.EarType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.EarType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.ears", next.name()));
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 2, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.ears.tooltip"))).build()));

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.tail", CustomLatexEntity.TailType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.TailType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.tail", next.name()));
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing * 2, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.tail.tooltip"))).build()));

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.legs", CustomLatexEntity.LegType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.LegType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.legs", next.name()));
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 3, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.legs.tooltip"))).build()));

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.arms", CustomLatexEntity.ArmType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.ArmType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.arms", next.name()));
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing * 3, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.arms.tooltip"))).build()));

        modificationWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.scale", CustomLatexEntity.ScaleType.fromFlags(menu.getConfiguredCustomLatex()).name()), button -> {
            int flags = menu.getConfiguredCustomLatex();
            var next = CustomLatexEntity.ScaleType.fromFlags(flags).cycle();
            menu.inputModifyConfig(next.setFlags(flags));
            button.setMessage(Component.translatable("changed.stasis.modify.scale", next.name()));
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 4, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.scale.tooltip"))).build()));

        initialized = true;

        checkButtonStates();
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double p_97755_, double p_97756_) {
        if (buttonScope == ButtonScope.DEFAULT)
            return super.mouseDragged(x, y, button, p_97755_, p_97756_);
        else
            return this.getFocused() != null && this.isDragging() && button == 0 ? this.getFocused().mouseDragged(x, y, button, p_97755_, p_97756_) : false;
    }
}