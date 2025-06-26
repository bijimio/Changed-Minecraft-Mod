package net.ltxprogrammer.changed.network.packet;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class SyncTransfurPacket implements ChangedPacket {
    record Listing(int form,
                   TransfurCause cause,
                   float progress,
                   boolean temporaryFromSuit, CompoundTag data) {
        static Listing fromStream(FriendlyByteBuf buf) {
            return new Listing(
                    buf.readInt(),
                    buf.readEnum(TransfurCause.class),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readAnySizeNbt());
        }

        void toStream(FriendlyByteBuf buf) {
            buf.writeInt(form);
            buf.writeEnum(cause);
            buf.writeFloat(progress);
            buf.writeBoolean(temporaryFromSuit);
            buf.writeNbt(data);
        }
    }

    private final Map<UUID, Listing> changedForms;
    private static final int NO_FORM = -1;

    public SyncTransfurPacket(Map<UUID, Listing> changedForms) {
        this.changedForms = changedForms;
    }

    public SyncTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readUUID(), Listing.fromStream(next))).forEach(pair ->
                    changedForms.put(pair.getFirst(), pair.getSecond()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms.entrySet(), (next, form) -> { next.writeUUID(form.getKey()); form.getValue().toStream(next); });
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            ClientLevel level = Minecraft.getInstance().level;
            Objects.requireNonNull(level);
            changedForms.forEach((uuid, listing) -> {
                Player player = level.getPlayerByUUID(uuid);
                if (player != null) {
                    final var variant = ProcessTransfur.setPlayerTransfurVariant(player,
                            ChangedRegistry.TRANSFUR_VARIANT.getValue(listing.form),
                            TransfurContext.hazard(listing.cause),
                            listing.progress,
                            listing.temporaryFromSuit);
                    if (variant != null)
                        variant.load(listing.data);
                }
            });
            context.setPacketHandled(true);
        }
        else if (context.getDirection().getReceptionSide().isServer()) { // Mirror packet
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                final Listing senderListing = this.changedForms.get(sender.getUUID());

                if (senderListing != null)
                    Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(),
                            new SyncTransfurPacket(Map.of(sender.getUUID(), senderListing)));
            }
            context.setPacketHandled(true);
        }
    }

    public static class Builder {
        private final Map<UUID, Listing> changedForms = new HashMap<>();

        public void addPlayer(Player player, boolean excludeNormal) {
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                changedForms.put(player.getUUID(),
                        new Listing(ChangedRegistry.TRANSFUR_VARIANT.getID(variant.getParent()),
                                variant.transfurContext.cause,
                                variant.transfurProgression,
                                variant.isTemporaryFromSuit(),
                                variant.save()));
            }, () -> {
                if (!excludeNormal)
                    changedForms.put(player.getUUID(),
                        new Listing(NO_FORM, TransfurCause.DEFAULT, 0f, false, new CompoundTag()));
            });
        }

        public boolean worthSending() {
            return !changedForms.isEmpty();
        }

        public SyncTransfurPacket build() {
            return new SyncTransfurPacket(changedForms);
        }

        public static SyncTransfurPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player, false);
            return builder.build();
        }
    }
}
