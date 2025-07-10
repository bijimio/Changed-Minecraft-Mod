package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class AbstractLatexItem extends ItemNameBlockItem {
    private final Supplier<? extends SpreadingLatexType> type;

    public AbstractLatexItem(Block block, Supplier<? extends SpreadingLatexType> type) {
        super(block, new Properties().food(Foods.DRIED_KELP));
        this.type = type;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), (player, variant) -> {
            if (variant.getLatexType().isHostileTo(type.get()))
                player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
        });
        final var variant = type.get().getTransfurVariant(TransfurCause.ATE_LATEX, level.random);
        ProcessTransfur.progressTransfur(entity, 11.0f, variant, TransfurContext.hazard(TransfurCause.ATE_LATEX));
        return super.finishUsingItem(itemStack, level, entity);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return super.useOn(context);

        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        BlockPos positionToCover = clickedState.isFaceSturdy(context.getLevel(), context.getClickedPos(), context.getClickedFace(), SupportType.FULL) ?
                context.getClickedPos().relative(context.getClickedFace()) : context.getClickedPos();

        BlockState originalState = context.getLevel().getBlockState(positionToCover);
        LatexCoverState originalCover = LatexCoverState.getAt(context.getLevel(), positionToCover);

        var event = new SpreadingLatexType.CoveringBlockEvent(type.get(), originalState, originalState,
                type.get().defaultCoverState(), positionToCover, context.getLevel());
        type.get().defaultCoverBehavior(event);
        if (Changed.postModEvent(event))
            return InteractionResult.FAIL;
        if (event.originalState == event.getPlannedState() && event.plannedCoverState == originalCover)
            return InteractionResult.FAIL;
        // TODO revisit config
        /*if (!Changed.config.server.canBlockBeCovered(event.plannedState.getBlock()))
            return InteractionResult.FAIL;*/

        context.getLevel().setBlockAndUpdate(event.blockPos, event.getPlannedState());
        LatexCoverState.setAtAndUpdate(context.getLevel(), event.blockPos, event.plannedCoverState);

        event.getPostProcess().accept(context.getLevel(), positionToCover);

        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    public LatexType getLatexType() {
        return type.get();
    }
}
