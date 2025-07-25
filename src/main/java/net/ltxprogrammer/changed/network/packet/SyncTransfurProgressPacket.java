package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SyncTransfurProgressPacket implements ChangedPacket {
    private final UUID uuid;
    private final float progress;

    public SyncTransfurProgressPacket(UUID uuid, float progress) {
        this.uuid = uuid;
        this.progress = progress;
    }

    public SyncTransfurProgressPacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.progress = buffer.readFloat();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeFloat(progress);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var player = level.getPlayerByUUID(uuid);
                if (player == null)
                    return;
                var oldProgress = ProcessTransfur.getPlayerTransfurProgress(player);
                if (Math.abs(oldProgress - progress) < 0.02f) // Prevent sync shudder
                    return;
                ProcessTransfur.setPlayerTransfurProgress(player, progress);
                context.setPacketHandled(true);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
