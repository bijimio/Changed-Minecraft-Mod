package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ClientLevelExtension extends LevelExtension {
    public static final ClientLevelExtension INSTANCE = new ClientLevelExtension();

    @Override
    public void setCoversDirty(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState recordedState) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public void sendCoverUpdated(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState, int flags) {
        Minecraft.getInstance().levelRenderer.blockChanged(level, blockPos, Blocks.AIR.defaultBlockState, Blocks.AIR.defaultBlockState, flags);
    }
}
