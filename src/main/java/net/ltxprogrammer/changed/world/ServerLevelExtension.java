package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.packet.CustomLevelEventPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

public class ServerLevelExtension extends LevelExtension {
    public static final ServerLevelExtension INSTANCE = new ServerLevelExtension();

    @Override
    public void sendCoverUpdated(Level level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState, int flags) {
        ((ServerLevel)level).getChunkSource().blockChanged(blockPos);
    }

    @Override
    public void customLevelEvent(LevelAccessor level, @Nullable Player source, int id, BlockPos blockPos, int param) {
        ((ServerLevel)level).getServer().getPlayerList().broadcast(source,
                (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 64.0D,
                ((ServerLevel)level).dimension(),
                Changed.PACKET_HANDLER.toVanillaPacket(new CustomLevelEventPacket(id, blockPos, param, false), NetworkDirection.PLAY_TO_CLIENT));
    }
}
