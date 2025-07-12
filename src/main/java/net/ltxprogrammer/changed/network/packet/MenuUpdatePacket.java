package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.UpdateableMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class MenuUpdatePacket implements ChangedPacket {
    private final int containerId;
    private final CompoundTag payload;

    public MenuUpdatePacket(int containerId, CompoundTag payload) {
        this.containerId = containerId;
        this.payload = payload;
    }

    public MenuUpdatePacket(FriendlyByteBuf byteBuf) {
        containerId = byteBuf.readInt();
        payload = byteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(containerId);
        buffer.writeNbt(payload);
    }

    private ServerPlayer findPlayerFromContainerId(int id) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.containerMenu != null && player.containerMenu.containerId == id)
                return player;
        }

        return null;
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var player = UniversalDist.getLocalPlayer();
                if (player == null)
                    return;
                if (player.containerMenu == null || player.containerMenu.containerId != containerId)
                    return;
                if (player.containerMenu instanceof UpdateableMenu updateableMenu) {
                    updateableMenu.update(payload, context.getDirection().getReceptionSide(), null);
                }
            });
        }

        else {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var player = context.getSender();
                if (player.containerMenu instanceof UpdateableMenu updateableMenu && player.containerMenu.containerId == containerId) {
                    updateableMenu.update(payload, context.getDirection().getReceptionSide(), context.getSender());
                }
            });
        }
    }
}
