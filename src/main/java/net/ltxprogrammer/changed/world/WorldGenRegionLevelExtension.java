package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WorldGenRegionLevelExtension extends LevelExtension {
    public static final WorldGenRegionLevelExtension INSTANCE = new WorldGenRegionLevelExtension();

    @Override
    public boolean destroyLatexCover(LevelAccessor level, BlockPos blockPos, boolean doDrops, @Nullable Entity cause, int timeToLive) {
        LatexCoverState state = LatexCoverState.getAt(level, blockPos);
        if (state.isAir()) {
            return false;
        } else {
            if (doDrops) {
                LatexType.dropResources(state, ((WorldGenRegion)level).getLevel(), blockPos, cause, ItemStack.EMPTY);
            }

            return LatexCoverState.setAt(level, blockPos, ChangedLatexTypes.NONE.get().defaultCoverState(), 3, timeToLive);
        }
    }
}
