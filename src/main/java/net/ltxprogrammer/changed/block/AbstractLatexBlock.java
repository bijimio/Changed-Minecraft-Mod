package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexTypeOld;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractLatexBlock extends Block implements LatexCoveringSource {
    private final Supplier<? extends LatexType> latexType;
    private final Supplier<? extends Item> goo;

    public static LatexType getSurfaceType(LevelReader level, BlockPos blockPos, Direction face, SupportType supportType) {
        final LatexCoverState coverState = LatexCoverState.getAt(level, blockPos);
        final BlockState otherBlockState = level.getBlockState(blockPos.relative(face));
        if (!otherBlockState.isFaceSturdy(level, blockPos.relative(face), face.getOpposite(), supportType))
            return ChangedLatexTypes.NONE.get();
        if (!coverState.isAir())
            return coverState.getType();
        if (otherBlockState.getBlock() instanceof AbstractLatexBlock block)
            return block.latexType.get();
        return ChangedLatexTypes.NONE.get();
    }

    public static LatexType getSurfaceType(LevelReader level, BlockPos blockPos, Direction face) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL);
    }

    public static LatexType getSurfaceType(LatexCoverGetter level, BlockPos blockPos, Direction face, SupportType supportType) {
        final LatexCoverState coverState = level.getLatexCover(blockPos);
        final BlockState otherBlockState = level.getBlockState(blockPos.relative(face));
        if (!otherBlockState.isFaceSturdy(level, blockPos.relative(face), face.getOpposite(), supportType))
            return ChangedLatexTypes.NONE.get();
        if (!coverState.isAir())
            return coverState.getType();
        if (otherBlockState.getBlock() instanceof AbstractLatexBlock block)
            return block.latexType.get();
        return ChangedLatexTypes.NONE.get();
    }

    public static LatexType getSurfaceType(LatexCoverGetter level, BlockPos blockPos, Direction face) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL);
    }

    public static boolean isSurfaceOfType(LevelReader level, BlockPos blockPos, Direction face, LatexType type) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL) == type;
    }

    public static boolean isSurfaceOfType(LevelReader level, BlockPos blockPos, Direction face, SupportType supportType, LatexType type) {
        return getSurfaceType(level, blockPos, face, supportType) == type;
    }

    public static boolean isSurfaceOfType(LatexCoverGetter level, BlockPos blockPos, Direction face, LatexType type) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL) == type;
    }

    public static boolean isSurfaceOfType(LatexCoverGetter level, BlockPos blockPos, Direction face, SupportType supportType, LatexType type) {
        return getSurfaceType(level, blockPos, face, supportType) == type;
    }

    public AbstractLatexBlock(Properties p_49795_, Supplier<? extends LatexType> latexType, Supplier<? extends Item> goo) {
        super(p_49795_.randomTicks().dynamicShape());
        this.latexType = latexType;
        this.goo = goo;
    }

    public static boolean tryCover(Level level, BlockPos relative, LatexType type) {
        if (!(type instanceof SpreadingLatexType spreadingLatexType))
            return false;
        LatexCoverState originalCover = LatexCoverState.getAt(level, relative);
        if (!originalCover.isAir())
            return false;
        /*if (spreadingLatexType.shouldDecay(type.defaultCoverState(), level, relative))
            return false;*/
        BlockState old = level.getBlockState(relative);
        if (old.isCollisionShapeFullBlock(level, relative))
            return false;

        var event = new SpreadingLatexType.CoveringBlockEvent(spreadingLatexType, old, old,
                spreadingLatexType.spreadState(level, relative, spreadingLatexType.sourceCoverState()), relative, level);
        spreadingLatexType.defaultCoverBehavior(event);
        if (Changed.postModEvent(event))
            return false;
        if (event.originalState == event.getPlannedState() && event.plannedCoverState == originalCover)
            return false;
        /*if (!Changed.config.server.canBlockBeCovered(event.plannedState.getBlock()))
            return InteractionResult.FAIL;*/

        level.setBlockAndUpdate(event.blockPos, event.getPlannedState());
        LatexCoverState.setAtAndUpdate(level, event.blockPos, event.plannedCoverState);

        event.getPostProcess().accept(level, event.blockPos);
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    private static final float FACTION_BENEFIT = 1.1F;
    private static final float FACTION_HINDER = 0.5F;
    private static final float NEUTRAL_HINDER = 0.75F;

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        return false;
    }

    public static void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity, LatexType latexType) {
        ChangedEntity ChangedEntity = null;

        if (entity instanceof ChangedEntity) ChangedEntity = (ChangedEntity)entity;

        if (entity instanceof Player player) {
            TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (variant != null)
                ChangedEntity = variant.getChangedEntity();
        }

        if (ChangedEntity != null) {
            LatexType type = ChangedEntity.getLatexType();
            if (latexType.isFriendlyTo(type)) {
                if (!entity.isInWater())
                    multiplyMotion(entity, FACTION_BENEFIT);
            }

            else if (latexType.isHostileTo(type)) {
                multiplyMotion(entity, FACTION_HINDER);
            }

            else {
                multiplyMotion(entity, NEUTRAL_HINDER);
            }
        }

        else if (entity instanceof LivingEntity) {
            multiplyMotion(entity, NEUTRAL_HINDER);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        stepOn(level, blockPos, blockState, entity, latexType.get());
    }

    private static void multiplyMotion(Entity entity, float mul) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(mul, mul, mul));
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, builder.getParameter(LootContextParams.TOOL)) > 0)
            return List.of(new ItemStack(this));
        return List.of(goo.get().getDefaultInstance(), goo.get().getDefaultInstance(), goo.get().getDefaultInstance());
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull RandomSource random) {
        super.randomTick(state, level, position, random);

        latexTick(state, level, position, random);
    }

    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull RandomSource random) {}
}
