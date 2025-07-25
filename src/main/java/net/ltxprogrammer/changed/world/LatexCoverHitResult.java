package net.ltxprogrammer.changed.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatexCoverHitResult extends BlockHitResult {
    private final Direction direction;
    private final BlockPos blockPos;
    private final boolean inside;

    public static @Nullable BlockHitResult wrap(@Nullable BlockHitResult result) {
        if (result == null)
            return null;
        if (result instanceof LatexCoverHitResult)
            return result;
        if (result.getType() == Type.MISS)
            return result;
        return new LatexCoverHitResult(result.getLocation(), result.getDirection(), result.getBlockPos(), result.isInside());
    }

    public LatexCoverHitResult(Vec3 location, Direction direction, BlockPos blockPos, boolean inside) {
        super(location, direction, blockPos, inside);
        this.direction = direction;
        this.blockPos = blockPos;
        this.inside = inside;
    }

    public @NotNull BlockHitResult withDirection(@NotNull Direction direction) {
        return new LatexCoverHitResult(this.location, direction, this.blockPos, this.inside);
    }

    public @NotNull BlockHitResult withPosition(@NotNull BlockPos blockPos) {
        return new LatexCoverHitResult(this.location, this.direction, blockPos, this.inside);
    }
}
