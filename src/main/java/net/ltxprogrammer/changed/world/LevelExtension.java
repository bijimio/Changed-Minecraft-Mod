package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LevelExtension {
    public static final LevelExtension INSTANCE = new LevelExtension();

    public void setCoversDirty(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState recordedState) {

    }

    public void sendCoverUpdated(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState, int flags) {

    }

    public void coverUpdated(Level level, BlockPos blockPos, LatexType latexType) {

    }

    public void onLatexCoverStateChange(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState) {

    }
}
