package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.block.Pillow;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PillowBlockEntity extends BlockEntity implements SeatableBlockEntity {
    public SeatEntity entityHolder;
    private DyeColor color;

    public PillowBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.PILLOW.get(), pos, state);
        this.color = ((Pillow)this.getBlockState().getBlock()).getColor();
    }

    @Override
    public SeatEntity getEntityHolder() {
        return entityHolder;
    }

    @Override
    public void setEntityHolder(SeatEntity entityHolder) {
        this.entityHolder = entityHolder;
    }

    public boolean sitEntity(LivingEntity entity) {
        if (entityHolder == null || entityHolder.isRemoved()) {
            entityHolder = SeatEntity.createFor(entity.level(), this.getBlockState(), this.getBlockPos(), false);
        }

        if (this.getSeatedEntity() != null)
            return false;
        else if (entityHolder != null) {
            if (!level.isClientSide)
                entity.startRiding(entityHolder);
            return true;
        }

        return false;
    }

    public void forceOutEntity() {
        final var entity = this.getSeatedEntity();
        if (entity != null && entity.vehicle == entityHolder) {
            entity.vehicle = null;
        }
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PillowBlockEntity blockEntity) {}
}
