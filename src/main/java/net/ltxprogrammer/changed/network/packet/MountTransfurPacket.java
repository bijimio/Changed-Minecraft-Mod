package net.ltxprogrammer.changed.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class MountTransfurPacket implements ChangedPacket {
    private final UUID entity;
    private final UUID mount;

    public MountTransfurPacket(UUID entity, UUID mount) {
        this.entity = entity;
        this.mount = mount;
    }

    public MountTransfurPacket(FriendlyByteBuf buffer) {
        this.entity = buffer.readUUID();
        this.mount = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(entity);
        buffer.writeUUID(mount);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (entity.equals(mount))
                    level.getPlayerByUUID(entity).stopRiding();
                else
                    level.getPlayerByUUID(entity).startRiding(level.getPlayerByUUID(mount));
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
