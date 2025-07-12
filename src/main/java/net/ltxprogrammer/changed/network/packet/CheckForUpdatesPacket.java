package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class CheckForUpdatesPacket implements ChangedPacket {
    private final int serverVersion;

    public CheckForUpdatesPacket(int serverVersion) {
        this.serverVersion = serverVersion;
    }

    public CheckForUpdatesPacket(FriendlyByteBuf buffer) {
        serverVersion = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(serverVersion);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        context.setPacketHandled(true);
        if (PatreonBenefits.currentVersion == serverVersion)
            return CompletableFuture.completedFuture(null);
        else
            return CompletableFuture.supplyAsync(() -> {
                try {
                    PatreonBenefits.checkForUpdates();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
    }
}
