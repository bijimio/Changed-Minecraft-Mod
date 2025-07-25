package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedRegistry;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Packet intended to sync accessory slots with clients
 * Client copies all data into given entity
 * Server opens the sender's accessory menu
 */
public class AccessoryEventPacket implements ChangedPacket {
    private final int entityId;
    private final AccessorySlotType slotType;
    private final int eventId;

    public AccessoryEventPacket(int entityId, AccessorySlotType slotType, int eventId) {
        this.entityId = entityId;
        this.slotType = slotType;
        this.eventId = eventId;
    }

    public AccessoryEventPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.slotType = ChangedRegistry.ACCESSORY_SLOTS.readRegistryObject(buffer);
        this.eventId = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        ChangedRegistry.ACCESSORY_SLOTS.writeRegistryObject(buffer, slotType);
        buffer.writeInt(eventId);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(level.getEntity(entityId) instanceof LivingEntity livingEntity))
                    throw new IllegalStateException("Entity is not a living entity");

                AccessorySlots.getForEntity(livingEntity).flatMap(slots -> slots.getItem(slotType))
                        .ifPresent(itemStack -> slotType.handleEvent(livingEntity, itemStack, eventId));
            });
        }

        else {
            final var entity = context.getSender();
            AccessorySlots.getForEntity(entity).flatMap(slots -> slots.getItem(slotType))
                    .ifPresent(itemStack -> slotType.handleEvent(entity, itemStack, eventId));
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), this);
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }
    }
}
