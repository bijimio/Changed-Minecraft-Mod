package net.ltxprogrammer.changed.network;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.CustomFallable;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.network.packet.*;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedPackets {
    private final SimpleChannel packetHandler;
    private int messageID = 0;

    private record DeferredPacket(String name, Supplier<Boolean> canRun, Runnable handle, AtomicInteger tries) {
        static <T extends ChangedPacket> DeferredPacket of(T packet, BiConsumer<T, Supplier<NetworkEvent.Context>> handler, Supplier<NetworkEvent.Context> contextSupplier) {
            final var context = contextSupplier.get();
            return new DeferredPacket(packet.getClass().getSimpleName(),
                    () -> packet.canBeHandled(() -> context),
                    () -> handler.accept(packet, () -> context),
                    new AtomicInteger(0));
        }
    }

    private final Queue<DeferredPacket> deferredClientPackets = new ArrayDeque<>();
    private final Queue<DeferredPacket> deferredServerPackets = new ArrayDeque<>();

    public ChangedPackets(SimpleChannel packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void tryHandlePackets(LogicalSide side) {
        final var deferredPackets = switch (side) {
            case CLIENT -> deferredClientPackets;
            case SERVER -> deferredServerPackets;
        };

        while (!deferredPackets.isEmpty()) {
            final var packet = deferredPackets.element();
            if (!packet.canRun().get()) {
                final int nowTries = packet.tries().incrementAndGet();
                if (nowTries > 5) {
                    deferredPackets.remove();
                    Changed.LOGGER.warn("Dropped deferred packet {} after {} tries", packet.name(), nowTries);
                    continue;
                }

                break;
            }

            deferredPackets.remove();
            Changed.LOGGER.warn("Handling deferred packet {} after {} tries", packet.name(), packet.tries().incrementAndGet());
            packet.handle().run();
        }
    }

    public void registerPackets() {
        addNetworkMessage(CheckForUpdatesPacket.class, CheckForUpdatesPacket::new);
        addNetworkMessage(GrabEntityPacket.class, GrabEntityPacket::new);
        addNetworkMessage(GrabEntityPacket.GrabKeyState.class, GrabEntityPacket.GrabKeyState::new);
        addNetworkMessage(GrabEntityPacket.EscapeKeyState.class, GrabEntityPacket.EscapeKeyState::new);
        addNetworkMessage(GrabEntityPacket.AnnounceEscapeKey.class, GrabEntityPacket.AnnounceEscapeKey::new);
        addNetworkMessage(MountTransfurPacket.class, MountTransfurPacket::new);
        addNetworkMessage(SyncSwitchPacket.class, SyncSwitchPacket::new);
        addNetworkMessage(SyncTransfurPacket.class, SyncTransfurPacket::new);
        addNetworkMessage(SyncTransfurProgressPacket.class, SyncTransfurProgressPacket::new);
        addNetworkMessage(QueryTransfurPacket.class, QueryTransfurPacket::new);
        addNetworkMessage(VariantAbilityActivate.class, VariantAbilityActivate::new);
        addNetworkMessage(SyncVariantAbilityPacket.class, SyncVariantAbilityPacket::new);
        addNetworkMessage(MenuUpdatePacket.class, MenuUpdatePacket::new);
        addNetworkMessage(EmotePacket.class, EmotePacket::new);
        addNetworkMessage(SyncMoversPacket.class, SyncMoversPacket::new);
        addNetworkMessage(ServerboundSetGluBlockPacket.class, ServerboundSetGluBlockPacket::new);
        addNetworkMessage(BasicPlayerInfoPacket.class, BasicPlayerInfoPacket::new);
        addNetworkMessage(SetTransfurVariantDataPacket.class, SetTransfurVariantDataPacket::new);
        addNetworkMessage(TugCameraPacket.class, TugCameraPacket::new);
        addNetworkMessage(ExtraJumpKeybind.class, ExtraJumpKeybind::buffer, ExtraJumpKeybind::new, ExtraJumpKeybind::handler);
        addNetworkMessage(CustomFallable.UpdateFallingBlockEntityData.class, CustomFallable.UpdateFallingBlockEntityData::new);
        addNetworkMessage(SeatEntityInfoPacket.class, SeatEntityInfoPacket::new);
        addNetworkMessage(TransfurEntityEventPacket.class, TransfurEntityEventPacket::new);
        addNetworkMessage(AbilityPayloadPacket.class, AbilityPayloadPacket::new);
        addNetworkMessage(MultiRotateHeadPacket.class, MultiRotateHeadPacket::new);
        addNetworkMessage(AnimationEventPacket.class, AnimationEventPacket::new);
        addNetworkMessage(AccessoryEntities.SyncPacket.class, AccessoryEntities.SyncPacket::new);
        addNetworkMessage(AccessorySyncPacket.class, AccessorySyncPacket::new);
        addNetworkMessage(AccessoryEventPacket.class, AccessoryEventPacket::new);
    }

    private <T> BiConsumer<T, FriendlyByteBuf> wrapEncoder(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder) {
        return (packet, buffer) -> {
            try {
                encoder.accept(packet, buffer);
            } catch (Exception e) {
                throw new RuntimeException("Exception while encoding " + messageType.getSimpleName() + ": " + e, e);
            }
        };
    }

    private <T> Function<FriendlyByteBuf, T> wrapDecoder(Class<T> messageType, Function<FriendlyByteBuf, T> decoder) {
        return buffer -> {
            try {
                return decoder.apply(buffer);
            } catch (Exception e) {
                throw new RuntimeException("Exception while decoding " + messageType.getSimpleName() + ": " + e, e);
            }
        };
    }

    private <T extends ChangedPacket> BiConsumer<T, Supplier<NetworkEvent.Context>> wrapHandler(Class<T> messageType, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        return (packet, context) -> {
            try {
                if (!packet.canBeHandled(context)) {
                    Changed.LOGGER.info("Deferred packet that was handled too early: {}", messageType.getSimpleName());
                    if (context.get().getDirection().getReceptionSide().isClient())
                        deferredClientPackets.add(DeferredPacket.of(packet, handler, context));
                    else
                        deferredServerPackets.add(DeferredPacket.of(packet, handler, context));
                    return;
                }
                handler.accept(packet, context);
            } catch (Exception e) {
                throw new RuntimeException("Exception while handling " + messageType.getSimpleName() + ": " + e, e);
            }
        };
    }

    private <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                              BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        packetHandler.registerMessage(messageID++, messageType,
                wrapEncoder(messageType, encoder),
                wrapDecoder(messageType, decoder),
                handler);
    }

    private <T extends ChangedPacket> void addNetworkMessage(Class<T> messageType, Function<FriendlyByteBuf, T> ctor) {
        packetHandler.registerMessage(messageID++, messageType,
                wrapEncoder(messageType, T::write),
                wrapDecoder(messageType, ctor),
                wrapHandler(messageType, T::handle));
    }
}
