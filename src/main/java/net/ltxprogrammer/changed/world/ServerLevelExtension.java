package net.ltxprogrammer.changed.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class ServerLevelExtension extends LevelExtension {
    public static final ServerLevelExtension INSTANCE = new ServerLevelExtension();

    @Override
    public void sendCoverUpdated(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState, int flags) {
        ((ServerLevel)level).getChunkSource().blockChanged(blockPos);
    }
}
