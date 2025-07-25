package net.ltxprogrammer.changed.mixin.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow private static CreativeModeTab selectedTab;
    @Unique private static final ResourceLocation ACCESSORY_ICON = Changed.modResource("textures/gui/basic_player_info.png");

    @Unique private Button accessoryButton;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void addAccessoryButton(CallbackInfo ci) {
        accessoryButton = this.addRenderableWidget(new ImageButton(this.leftPos - 24, this.height / 2 - 22, 20, 20, 0, 0, 20, ACCESSORY_ICON, 20, 40, (button) -> {
            if (menu.inventoryMenu instanceof InventoryMenu invMenu)
                AccessorySlots.openAccessoriesMenu(invMenu.owner);
        }));

        accessoryButton.visible = selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }

    @Inject(method = "selectTab", at = @At("RETURN"))
    protected void updateAccessoryButtonVisibility(CreativeModeTab tab, CallbackInfo ci) {
        if (tab == null)
            return;
        if (accessoryButton != null)
            accessoryButton.visible = selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }
}
