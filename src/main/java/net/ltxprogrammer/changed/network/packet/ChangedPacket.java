package net.ltxprogrammer.changed.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public interface ChangedPacket {
    void write(FriendlyByteBuf buffer);

    CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor);

    @Deprecated
    default boolean canBeHandled(Supplier<NetworkEvent.Context> contextSupplier) {
        return true;
    }

    default Exception makeIllegalSideException(LogicalSide receivedSide) {
        return new IllegalStateException("Packet received on " + receivedSide + " cannot be handled");
    }

    default Exception makeIllegalSideException(LogicalSide receivedSide, LogicalSide expectedSide) {
        return new IllegalStateException("Packet received on " + receivedSide + " cannot be handled. Expected to receive on " + expectedSide);
    }

    static interface Handler<T extends ChangedPacket> {
        CompletableFuture<Void> accept(T packet, NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor);
    }
}
