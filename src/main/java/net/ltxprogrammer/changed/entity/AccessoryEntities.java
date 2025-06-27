package net.ltxprogrammer.changed.entity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.AccessorySyncPacket;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AccessoryAccessMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AccessoryEntities extends SimplePreparableReloadListener<Multimap<EntityType<?>, AccessorySlotType>> {
    public static final AccessoryEntities INSTANCE = new AccessoryEntities();
    private final Multimap<EntityType<?>, AccessorySlotType> validEntities = HashMultimap.create();

    public static EntityType<?> getApparentEntityType(LivingEntity entity) {
        entity = EntityUtil.maybeGetOverlaying(entity);
        if (entity instanceof CustomLatexEntity customLatex) {
            return customLatex.getEntityTypeForAccessories();
        }

        return entity.getType();
    }

    public void forceReloadAccessories(LivingEntity entity) {
        AccessorySlots.getForEntity(entity).ifPresent(slots -> {
            final var slotsBefore = slots.getSlotTypes().collect(Collectors.toSet());

            if (!slots.initialize(
                    canEntityTypeUseSlot(getApparentEntityType(entity)),
                    AccessorySlots.defaultInvalidHandler(entity)))
                return;

            final var slotsAfter = slots.getSlotTypes().collect(Collectors.toSet());

            if (!entity.level().isClientSide) {
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new AccessorySyncPacket(entity.getId(), slots));

                if (!slotsBefore.containsAll(slotsAfter) || !slotsAfter.containsAll(slotsBefore)) {
                    // Available slots changed, reopen menu for player

                    if (entity instanceof ServerPlayer player && player.containerMenu instanceof AccessoryAccessMenu menu && menu.owner == player) {
                        AccessoryAccessMenu.openForPlayer(player);
                    }
                }
            }
        });
    }

    public Predicate<AccessorySlotType> canEntityTypeUseSlot(EntityType<?> entityType) {
        final var allowedSlots = validEntities.get(entityType);
        return allowedSlots::contains;
    }

    private Multimap<EntityType<?>, AccessorySlotType> processJSONFile(JsonObject root) {
        Multimap<EntityType<?>, AccessorySlotType> working = HashMultimap.create();

        root.getAsJsonArray("entities").forEach(entity -> {
            final ResourceLocation entityId = ResourceLocation.parse(entity.getAsString());
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(entityId))
                throw new IllegalArgumentException("Unknown entity " + entityId);
            final EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityId);

            root.getAsJsonArray("slots").forEach(slot -> {
                working.put(entityType, ChangedRegistry.ACCESSORY_SLOTS.get().getValue(ResourceLocation.parse(slot.getAsString())));
            });
        });

        return working;
    }

    @Override
    @NotNull
    public Multimap<EntityType<?>, AccessorySlotType> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(HashMultimap.create(), resources, "accessories/entities", (map, filename, id, json) -> {
            map.putAll(processJSONFile(json));
        }, (exception, filename) -> Changed.LOGGER.error("Failed to load entities for AccessorySlots from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull Multimap<EntityType<?>, AccessorySlotType> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        validEntities.clear();
        validEntities.putAll(output);
    }

    public static class SyncPacket implements ChangedPacket {
        private final Multimap<EntityType<?>, AccessorySlotType> map;
        private final @Nullable AccessorySyncPacket receiverAccessories;

        protected SyncPacket(Multimap<EntityType<?>, AccessorySlotType> map) {
            this(map, null);
        }

        protected SyncPacket(Multimap<EntityType<?>, AccessorySlotType> map, AccessorySyncPacket receiverAccessories) {
            this.map = map;
            this.receiverAccessories = receiverAccessories;
        }

        public SyncPacket(FriendlyByteBuf buffer) {
            this.map = HashMultimap.create();
            buffer.readMap(FriendlyByteBuf::readInt, mapBuffer -> {
                return mapBuffer.readCollection(HashSet::new, FriendlyByteBuf::readInt);
            }).forEach((key, slots) -> {
                final EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.byId(key);
                for (var slotTypeId : slots) {
                    map.put(entityType, ChangedRegistry.ACCESSORY_SLOTS.getValue(slotTypeId));
                }
            });
            this.receiverAccessories = buffer.readOptional(AccessorySyncPacket::new).orElse(null);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            var intMap = new HashMap<Integer, Set<Integer>>();
            this.map.forEach((entityType, slotType) -> {
                intMap.computeIfAbsent(BuiltInRegistries.ENTITY_TYPE.getId(entityType), id -> new HashSet<>())
                        .add(ChangedRegistry.ACCESSORY_SLOTS.getID(slotType));
            });
            buffer.writeMap(intMap, FriendlyByteBuf::writeInt,
                    (setBuffer, intSet) -> setBuffer.writeCollection(intSet, FriendlyByteBuf::writeInt));
            buffer.writeOptional(Optional.ofNullable(this.receiverAccessories), (opt, v) -> v.write(opt));
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            var context = contextSupplier.get();

            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                AccessoryEntities.INSTANCE.validEntities.clear();
                AccessoryEntities.INSTANCE.validEntities.putAll(this.map);

                if (this.receiverAccessories != null) {
                    this.receiverAccessories.handle(contextSupplier);
                }

                context.setPacketHandled(true);
            }
        }

        @Override
        public boolean canBeHandled(Supplier<NetworkEvent.Context> contextSupplier) {
            return receiverAccessories == null || receiverAccessories.canBeHandled(contextSupplier);
        }
    }

    public SyncPacket syncPacket() {
        return new SyncPacket(this.validEntities);
    }

    public SyncPacket syncPacket(ServerPlayer receiver) {
        return AccessorySlots.getForEntity(receiver).map(
                slots -> new SyncPacket(this.validEntities, new AccessorySyncPacket(receiver.getId(), slots))
        ).orElseGet(this::syncPacket);
    }
}
