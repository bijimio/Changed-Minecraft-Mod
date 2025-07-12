package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public void customLevelEvent(LevelAccessor level, @Nullable Player source, int type, BlockPos pos, int data) {
        final ClientLevel clientLevel = (ClientLevel)level;
        switch (type) {
            case 2001:
                LatexCoverState coverState = ChangedLatexTypes.getLatexCoverStateIDMap().byId(data);
                if (!coverState.isAir()) {
                    SoundType soundtype = coverState.getSoundType(level, pos, null);
                    if (soundtype != null)
                        clientLevel.playLocalSound(pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);

                    Block block = coverState.getType().getBlock();
                    if (block != null)
                        clientLevel.addDestroyBlockEffect(pos, block.defaultBlockState());
                }
                break;
        }
    }
}
