package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.inventory.AccessoryAccessMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Packet intended to sync accessory slots with clients
 * Client copies all data into given entity
 * Server opens the sender's accessory menu
 */
public class AccessorySyncPacket implements ChangedPacket {
    private final int entityId;
    private final boolean partial;
    private final AccessorySlots slots;

    public AccessorySyncPacket(int entityId, AccessorySlots slots) {
        this.entityId = entityId;
        this.partial = false;
        this.slots = slots;
    }

    public AccessorySyncPacket(int entityId, Map<AccessorySlotType, ItemStack> slots) {
        this.entityId = entityId;
        this.partial = true;
        this.slots = new AccessorySlots(null);
        this.slots.initialize(slots::containsKey, ignored -> {});
        slots.forEach(this.slots::setItem);
    }

    public AccessorySyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.partial = buffer.readBoolean();
        this.slots = new AccessorySlots(null);
        slots.readNetwork(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeBoolean(partial);
        slots.writeNetwork(buffer);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(level.getEntity(entityId) instanceof LivingEntity livingEntity))
                    throw new IllegalStateException("Entity is not a living entity");

                AccessorySlots.getForEntity(livingEntity).ifPresent(accessorySlots -> accessorySlots.setAll(this.slots, !partial));
            });
        }

        else {
            var sender = context.getSender();
            if (sender == null)
                return CompletableFuture.failedFuture(new IllegalStateException("Sender is null (Shouldn't be possible)"));

            context.setPacketHandled(true);
            AccessoryAccessMenu.openForPlayer(sender);

            return CompletableFuture.completedFuture(null);
        }
    }
}
