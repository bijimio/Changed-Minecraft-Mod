package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SyncVariantAbilityPacket implements ChangedPacket {
    private final AbstractAbility<?> ability;
    private final CompoundTag data;
    private final UUID playerUUID; // Field is ignored when server receives packet

    public SyncVariantAbilityPacket(AbstractAbility<?> ability, CompoundTag data) {
        this.ability = ability;
        this.data = data;
        this.playerUUID = UUID.randomUUID();
    }

    public SyncVariantAbilityPacket(AbstractAbility<?> ability, CompoundTag data, UUID uuid) {
        this.ability = ability;
        this.data = data;
        this.playerUUID = uuid;
    }

    public SyncVariantAbilityPacket(FriendlyByteBuf buffer) {
        this.ability = ChangedRegistry.ABILITY.readRegistryObject(buffer);
        this.data = buffer.readNbt();
        this.playerUUID = buffer.readUUID();
    }

    public void write(FriendlyByteBuf buffer) {
        ChangedRegistry.ABILITY.writeRegistryObject(buffer, ability);
        buffer.writeNbt(data);
        buffer.writeUUID(playerUUID);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                Player affectedPlayer = level.getPlayerByUUID(playerUUID);
                ProcessTransfur.ifPlayerTransfurred(affectedPlayer, variant -> {
                    if (variant.abilityInstances.containsKey(ability))
                        variant.abilityInstances.get(ability).readData(data);
                });
            });
        }

        else {
            ServerPlayer sender = context.getSender();
            ProcessTransfur.ifPlayerTransfurred(sender, variant -> {
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(this.ability, data, sender.getUUID()));
                if (variant.abilityInstances.containsKey(ability))
                    variant.abilityInstances.get(ability).readData(data);
            });
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }
    }
}
