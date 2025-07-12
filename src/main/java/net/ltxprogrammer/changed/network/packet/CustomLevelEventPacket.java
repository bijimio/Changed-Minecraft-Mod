package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class CustomLevelEventPacket implements ChangedPacket {
    private final int type;
    private final BlockPos pos;
    private final int data;
    private final boolean globalEvent;

    public CustomLevelEventPacket(int type, BlockPos pos, int data, boolean globalEvent) {
        this.type = type;
        this.pos = pos;
        this.data = data;
        this.globalEvent = globalEvent;
    }

    public CustomLevelEventPacket(FriendlyByteBuf buffer) {
        this.type = buffer.readInt();
        this.pos = buffer.readBlockPos();
        this.data = buffer.readInt();
        this.globalEvent = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.type);
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.data);
        buffer.writeBoolean(this.globalEvent);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                UniversalDist.getLevelExtension(level)
                        .customLevelEvent(level, type, pos, data);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
