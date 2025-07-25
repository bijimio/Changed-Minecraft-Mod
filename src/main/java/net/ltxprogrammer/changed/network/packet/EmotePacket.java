package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.process.ProcessEmote;
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

public class EmotePacket implements ChangedPacket {
    private final UUID entity;
    private final Emote emote;

    public EmotePacket(UUID entity, Emote emote) {
        this.entity = entity;
        this.emote = emote;
    }

    public EmotePacket(FriendlyByteBuf buffer) {
        this.entity = buffer.readUUID();
        this.emote = Emote.values()[buffer.readInt()];
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(entity);
        buffer.writeInt(emote.ordinal());
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                ProcessEmote.playerEmote(level.getPlayerByUUID(entity), emote);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
