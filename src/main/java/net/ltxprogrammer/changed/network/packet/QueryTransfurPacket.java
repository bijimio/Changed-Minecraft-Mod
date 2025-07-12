package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class QueryTransfurPacket implements ChangedPacket {
    private final List<UUID> changedForms;
    private static final ResourceLocation NO_FORM = Changed.modResource("no_form");

    public QueryTransfurPacket(List<UUID> changedForms) {
        this.changedForms = changedForms;
    }

    public QueryTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = buffer.readList(FriendlyByteBuf::readUUID);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms, FriendlyByteBuf::writeUUID);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                ServerPlayer sender = context.getSender();
                if (sender != null) {
                    SyncTransfurPacket.Builder builder = new SyncTransfurPacket.Builder();
                    changedForms.forEach(uuid -> {
                        Player player = sender.level().getPlayerByUUID(uuid);
                        if (player != null)
                            builder.addPlayer(player, false);
                    });
                    if (builder.worthSending()) Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender), builder.build());
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.SERVER));
    }

    public static class Builder {
        private final List<UUID> changedForms = new ArrayList<>();

        public void addPlayer(Player player) {
            changedForms.add(player.getUUID());
        }

        public QueryTransfurPacket build() {
            return new QueryTransfurPacket(changedForms);
        }

        public static QueryTransfurPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
