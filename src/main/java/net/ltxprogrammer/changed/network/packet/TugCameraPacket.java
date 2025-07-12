package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class TugCameraPacket implements ChangedPacket {
    private final CameraUtil.TugData tug;

    public TugCameraPacket(CameraUtil.TugData tug) {
        this.tug = tug;
    }

    public TugCameraPacket(FriendlyByteBuf buffer) {
        this.tug = CameraUtil.TugData.readFromBuffer(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        this.tug.writeToBuffer(buffer);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(UniversalDist.getLocalPlayer() instanceof PlayerDataExtension ext))
                    return;
                ext.setTugData(this.tug);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
