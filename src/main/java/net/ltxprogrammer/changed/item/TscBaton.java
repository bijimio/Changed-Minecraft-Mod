package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TscBaton extends TscWeapon implements SpecializedItemRendering {
    public TscBaton() {
        super(new Properties().durability(500));
    }

    private static final ModelResourceLocation BATON_INVENTORY =
            new ModelResourceLocation(Changed.modResource("tsc_baton"), "inventory");
    private static final ModelResourceLocation BATON_IN_HAND =
            new ModelResourceLocation(Changed.modResource("tsc_baton_in_hand"), "inventory");
    private static final ModelResourceLocation BATON_IN_HAND_EMISSIVE =
            new ModelResourceLocation(Changed.modResource("tsc_baton_in_hand_emissive"), "inventory");

    @Nullable
    @Override
    public ModelResourceLocation getEmissiveModelLocation(ItemStack itemStack, ItemDisplayContext type) {
        return SpecializedItemRendering.isGUI(type) ? null : BATON_IN_HAND_EMISSIVE;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type) {
        return SpecializedItemRendering.isGUI(type) ? BATON_INVENTORY : BATON_IN_HAND;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> loader) {
        loader.accept(BATON_IN_HAND);
        loader.accept(BATON_IN_HAND_EMISSIVE);
    }

    public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity source) {
        sweepWeapon(source, attackRange());
        applyShock(enemy, attackStun());
        itemStack.hurtAndBreak(1, source, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return blockState.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
    }

    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
        if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(2, entity, (living) -> {
                living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.swingTime > 0)
            return true;
        sweepWeapon(entity, attackRange());
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public int attackStun() {
        return 5;
    }

    @Override
    public double attackDamage() {
        return 1 + Tiers.IRON.getAttackDamageBonus();
    }

    @Override
    public double attackSpeed() {
        return -2.4;
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }
}
