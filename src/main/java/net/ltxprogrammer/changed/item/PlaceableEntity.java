package net.ltxprogrammer.changed.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class PlaceableEntity<T extends Entity> extends Item {
    private final Supplier<EntityType<T>> entityType;

    public PlaceableEntity(Properties builder, Supplier<EntityType<T>> entityType) {
        super(builder);
        this.entityType = entityType;
    }

    protected void finalizeEntity(T entity, ItemStack itemStack) {

    }

    protected T placeAndShrink(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos placePos = context.getClickedPos();

        if (level.isClientSide)
            return null;

        T entity = entityType.get().create(level);
        if (entity == null)
            return null;

        entity.setPos(placePos.getX() + 0.5, placePos.getY(), placePos.getZ() + 0.5);
        level.addFreshEntity(entity);
        this.finalizeEntity(entity, context.getItemInHand());
        context.getItemInHand().shrink(1);
        return entity;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);

        if (!level.isClientSide) {
            if (this.placeAndShrink(placeContext) == null)
                return InteractionResult.PASS;
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
