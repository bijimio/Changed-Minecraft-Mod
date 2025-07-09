package net.ltxprogrammer.changed.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface LatexCoverGetter extends BlockGetter {
    LatexCoverState getLatexCover(BlockPos blockPos);

    default BlockHitResult clip(ClipContext context) {
        final LatexCoverState.ShapeGetter shapeGetter = LatexCoverState.LatexCoverShapeGetter.wrap(context.block);
        return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, (localContext, blockPos) -> {
            BlockState blockstate = this.getBlockState(blockPos);
            FluidState fluidstate = this.getFluidState(blockPos);
            LatexCoverState coverState = this.getLatexCover(blockPos);
            Vec3 from = localContext.getFrom();
            Vec3 to = localContext.getTo();
            VoxelShape blockShape = localContext.getBlockShape(blockstate, this, blockPos);
            BlockHitResult blockResult = this.clipWithInteractionOverride(from, to, blockPos, blockShape, blockstate);
            VoxelShape fluidShape = localContext.getFluidShape(fluidstate, this, blockPos);
            BlockHitResult fluidResult = fluidShape.clip(from, to, blockPos);
            VoxelShape coverShape = shapeGetter.get(coverState, this, blockPos, localContext.collisionContext);
            BlockHitResult coverResult = this.clipWithInteractionOverride(from, to, blockPos, coverShape, coverState);
            double blockDistance = blockResult == null ? Double.MAX_VALUE : localContext.getFrom().distanceToSqr(blockResult.getLocation());
            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : localContext.getFrom().distanceToSqr(fluidResult.getLocation());
            double coverDistance = coverResult == null ? Double.MAX_VALUE : localContext.getFrom().distanceToSqr(coverResult.getLocation());
            if (blockDistance <= fluidDistance && blockDistance <= coverDistance)
                return blockResult;
            if (fluidDistance <= blockDistance && fluidDistance <= coverDistance)
                return fluidResult;
            return coverResult;
        }, (localContext) -> {
            Vec3 vec3 = localContext.getFrom().subtract(localContext.getTo());
            return BlockHitResult.miss(localContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(localContext.getTo()));
        });
    }

    default BlockHitResult clip(ClipContext context, @Nullable BlockHitResult regularResult) {
        final LatexCoverState.ShapeGetter shapeGetter = LatexCoverState.LatexCoverShapeGetter.wrap(context.block);
        return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, (localContext, blockPos) -> {
            LatexCoverState coverState = this.getLatexCover(blockPos);
            Vec3 from = localContext.getFrom();
            Vec3 to = localContext.getTo();
            VoxelShape voxelshape = shapeGetter.get(coverState, this, blockPos, localContext.collisionContext);
            BlockHitResult blockhitresult = this.clipWithInteractionOverride(from, to, blockPos, voxelshape, coverState);
            double d0 = blockhitresult == null ? Double.MAX_VALUE : localContext.getFrom().distanceToSqr(blockhitresult.getLocation());
            double d1 = regularResult == null ? Double.MAX_VALUE : localContext.getFrom().distanceToSqr(regularResult.getLocation());
            return d0 <= d1 ? blockhitresult : null;
        }, (localContext) -> regularResult);
    }

    @Nullable
    default BlockHitResult clipWithInteractionOverride(Vec3 from, Vec3 to, BlockPos blockPos, VoxelShape shape, LatexCoverState state) {
        BlockHitResult regularResult = LatexCoverHitResult.wrap(shape.clip(from, to, blockPos));
        if (regularResult != null) {
            BlockHitResult interactionResult = LatexCoverHitResult.wrap(state.getInteractionShape(this, blockPos).clip(from, to, blockPos));
            if (interactionResult != null && interactionResult.getLocation().subtract(from).lengthSqr() < regularResult.getLocation().subtract(from).lengthSqr()) {
                return regularResult.withDirection(interactionResult.getDirection());
            }
        }

        return regularResult;
    }

    public static LatexCoverGetter wrap(LevelAccessor level) {
        return new LatexCoverGetter() {
            @Override
            public LatexCoverState getLatexCover(BlockPos blockPos) {
                return LatexCoverState.getAt(level, blockPos);
            }

            @Override
            public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
                return level.getBlockEntity(blockPos);
            }

            @Override
            public BlockState getBlockState(BlockPos blockPos) {
                return level.getBlockState(blockPos);
            }

            @Override
            public FluidState getFluidState(BlockPos blockPos) {
                return level.getFluidState(blockPos);
            }

            @Override
            public int getHeight() {
                return level.getHeight();
            }

            @Override
            public int getMinBuildHeight() {
                return level.getMinBuildHeight();
            }
        };
    }

    public static LatexCoverGetter wrap(ChunkAccess chunk) {
        return new LatexCoverGetter() {
            @Override
            public LatexCoverState getLatexCover(BlockPos blockPos) {
                return LatexCoverState.getAt(chunk, blockPos);
            }

            @Override
            public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
                return chunk.getBlockEntity(blockPos);
            }

            @Override
            public BlockState getBlockState(BlockPos blockPos) {
                return chunk.getBlockState(blockPos);
            }

            @Override
            public FluidState getFluidState(BlockPos blockPos) {
                return chunk.getFluidState(blockPos);
            }

            @Override
            public int getHeight() {
                return chunk.getHeight();
            }

            @Override
            public int getMinBuildHeight() {
                return chunk.getMinBuildHeight();
            }
        };
    }

    public static LatexCoverGetter extend(BlockGetter level, Function<BlockPos, LatexCoverState> getter) {
        return new LatexCoverGetter() {
            @Override
            public LatexCoverState getLatexCover(BlockPos blockPos) {
                return getter.apply(blockPos);
            }

            @Override
            public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
                return level.getBlockEntity(blockPos);
            }

            @Override
            public BlockState getBlockState(BlockPos blockPos) {
                return level.getBlockState(blockPos);
            }

            @Override
            public FluidState getFluidState(BlockPos blockPos) {
                return level.getFluidState(blockPos);
            }

            @Override
            public int getHeight() {
                return level.getHeight();
            }

            @Override
            public int getMinBuildHeight() {
                return level.getMinBuildHeight();
            }
        };
    }
}
