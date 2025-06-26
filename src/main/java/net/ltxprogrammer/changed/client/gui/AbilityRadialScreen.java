package net.ltxprogrammer.changed.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Registry;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityRadialScreen extends VariantRadialScreen<AbilityRadialMenu> {
    public final AbilityRadialMenu menu;
    public final TransfurVariantInstance<?> variant;
    public final List<AbstractAbility<?>> abilities;

    public AbilityRadialScreen(AbilityRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, menu.variant);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        this.variant = menu.variant;
        this.abilities = menu.variant.abilityInstances.keySet().stream().filter(ability ->
                ability != ChangedAbilities.SELECT_HAIRSTYLE.get() || ability.canUse(IAbstractChangedEntity.forPlayer(menu.player)))
                .collect(Collectors.toList());
    }

    @Override
    public int getCount() {
        return abilities.size();
    }

    @Nullable
    @Override
    public List<Component> tooltipsFor(int section) {
        var abilityInstance = menu.variant.getAbilityInstance(abilities.get(section));
        if (abilityInstance == null)
            return List.of();

        var desc = abilityInstance.getAbilityDescription();
        var builder = ImmutableList.<Component>builder().add(abilityInstance.getAbilityName());

        if (!desc.isEmpty()) {
            builder.add(Component.empty());
            builder.addAll(desc);
        }

        if (this.minecraft.options.advancedItemTooltips)
            builder.add((Component.literal(ChangedRegistry.ABILITY.getKey(abilityInstance.ability).toString())).withStyle(ChatFormatting.DARK_GRAY));

        return builder.build();
    }

    @Override
    public void renderSectionForeground(GuiGraphics graphics, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue, float alpha) {
        boolean enabled = false;
        boolean selected = false;
        if (menu.variant.abilityInstances.containsKey(abilities.get(section))) {
            var ability = menu.variant.abilityInstances.get(abilities.get(section));
            if (ability != null) {
                enabled = ability.canUse();
            }
        }

        var ability = abilities.get(section);
        if (ability == ChangedAbilities.SELECT_HAIRSTYLE.get()) {
            x = x * 0.9;
            y = (y * 0.9) - 16;

            HairStyleRadialScreen.renderEntityHeadWithHair(graphics, (int)x + this.leftPos, (int)y + 32 + this.topPos, 40,
                    (float)(this.leftPos) - mouseX + (int)x,
                    (float)(this.topPos) - mouseY + (int)y,
                    variant.getChangedEntity(), alpha);
        }

        else {
            ChangedClient.abilityRenderer.getOrThrow().renderAndDecorateAbility(
                    menu.player,
                    menu.variant.getAbilityInstance(abilities.get(section)),
                    (int) (x - 24 + this.leftPos),
                    (int) (y - 24 + this.topPos),
                    48,
                    (enabled ? 1 : 0.5f),
                    enabled,
                    0);

            /*RenderSystem.setShaderTexture(0, abilities.get(section).getTexture(IAbstractChangedEntity.forPlayer(menu.player)));
            if (enabled) {
                RenderSystem.setShaderColor(0, 0, 0, 0.5f); // Render ability shadow
                GuiComponent.blit(pose, (int)x - 24 + this.leftPos, (int)y - 24 + this.topPos + 4, 0, 0, 48, 48, 48, 48);
            }
            RenderSystem.setShaderColor(red, green, blue, (enabled ? 1 : 0.5f) * alpha);
            GuiComponent.blit(pose, (int)x - 24 + this.leftPos, (int)y - 24 + this.topPos, 0, 0, 48, 48, 48, 48);*/
        }
    }

    @Override
    public boolean handleClicked(int section, SingleRunnable close) {
        close.run();
        var ability = abilities.get(section);
        variant.setSelectedAbility(ability);
        Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(this.menu.player, variant.abilityKeyState, ability));
        return false;
    }

    @Override
    public boolean isSelected(int section) {
        if (abilities.size() > section && menu.variant.abilityInstances.containsKey(abilities.get(section))) {
            var ability = menu.variant.abilityInstances.get(abilities.get(section));
            if (ability != null) {
                return menu.variant.selectedAbility == ability.ability;
            }
        }

        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        variant.resetTicksSinceLastAbilityActivity();
    }
}