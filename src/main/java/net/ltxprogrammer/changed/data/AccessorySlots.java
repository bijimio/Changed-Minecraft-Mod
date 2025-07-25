package net.ltxprogrammer.changed.data;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.ltxprogrammer.changed.network.packet.AccessoryEventPacket;
import net.ltxprogrammer.changed.network.packet.AccessorySyncPacket;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccessorySlots implements Container {
    private static final Logger LOGGER = LogManager.getLogger(AccessorySlots.class);

    public final @Nullable LivingEntity owner;
    private final Map<AccessorySlotType, ItemStack> items = new HashMap<>();
    private final Map<AccessorySlotType, ItemStack> lastItems = new HashMap<>();
    private final List<ItemStack> invalidItems = new ArrayList<>();
    private final Cacheable<List<AccessorySlotType>> orderedSlots = Cacheable.of(() -> {
        final var registry = ChangedRegistry.ACCESSORY_SLOTS;
        final var sorted = new ArrayList<>(registry.get().getValues().stream()
                .filter(items::containsKey)
                .map(slotType -> Pair.of(slotType, registry.getID(slotType)))
                .toList());
        sorted.sort(Comparator.comparingInt(Pair::getSecond));
        return sorted.stream().map(Pair::getFirst).toList();
    });

    public static final AccessorySlots DUMMY = new AccessorySlots(null);

    public static void openAccessoriesMenu(@NotNull LivingEntity entity) {
        if (entity.level().isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new AccessorySyncPacket(entity.getId(), AccessorySlots.DUMMY));
    }

    public AccessorySlots(@Nullable LivingEntity owner) {
        this.owner = owner;
    }

    public AccessorySlots() {
        this.owner = null;
    }

    /**
     * Slots are indexed in the order they are registered
     * @param slot
     * @return
     */
    public @Nullable AccessorySlotType getSlotTypeByIndex(int slot) {
        var sorted = orderedSlots.get();
        if (sorted.isEmpty() || sorted.size() <= slot)
            return null;
        return sorted.get(slot);
    }

    public List<AccessorySlotType> getOrderedSlots() {
        return orderedSlots.get();
    }

    public @Nullable Integer getSlotIndexByType(AccessorySlotType slotType) {
        var sorted = orderedSlots.get();
        for (int i = 0; i < sorted.size(); ++i)
            if (sorted.get(i) == slotType)
                return i;
        return null;
    }

    public static Optional<AccessorySlots> getForEntity(LivingEntity livingEntity) {
        if (livingEntity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null)
            return getForEntity(changedEntity.getUnderlyingPlayer());
        if (livingEntity instanceof LivingEntityDataExtension extension)
            return extension.getAccessorySlots();
        return Optional.empty();
    }

    public static boolean isWearing(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        return getForEntity(livingEntity).map(slots -> slots.getItems().anyMatch(predicate)).orElse(false);
    }

    public static boolean isSlotAvailable(LivingEntity livingEntity, AccessorySlotType slot) {
        return getForEntity(livingEntity).flatMap(slots -> slots.getItem(slot)).map(ItemStack::isEmpty).orElse(false);
    }

    public static boolean tryReplaceSlot(LivingEntity livingEntity, AccessorySlotType slot, ItemStack itemStack) {
        return getForEntity(livingEntity)
                .filter(slots -> slots.hasSlot(slot))
                .map(slots -> {
                    final var invalidHandler = defaultInvalidHandler(livingEntity);

                    var oldStack = slots.getItem(slot);
                    oldStack.ifPresent(invalidHandler);
                    slots.setItem(slot, itemStack);

                    if (itemStack.isEmpty())
                        return true;

                    slots.getSlotTypes().filter(otherSlot -> otherSlot != slot).forEach(otherSlot -> {
                        final var otherStack = slots.getItem(otherSlot).orElse(ItemStack.EMPTY);
                        if (otherStack.isEmpty())
                            return;

                        if (itemStack.getItem() instanceof AccessoryItem accessoryItem &&
                                (!accessoryItem.allowedWith(itemStack, otherStack, livingEntity, slot, otherSlot) ||
                                        accessoryItem.shouldDisableSlot(new AccessorySlotContext<>(livingEntity, slot, itemStack), otherSlot))) {
                            invalidHandler.accept(otherStack);
                            slots.setItem(otherSlot, ItemStack.EMPTY);
                        }
                        if (otherStack.getItem() instanceof AccessoryItem accessoryItem &&
                                (!accessoryItem.allowedWith(otherStack, itemStack, livingEntity, otherSlot, slot) ||
                                        accessoryItem.shouldDisableSlot(new AccessorySlotContext<>(livingEntity, otherSlot, otherStack), slot))) {
                            invalidHandler.accept(otherStack);
                            slots.setItem(otherSlot, ItemStack.EMPTY);
                        }
                    });

                    return true;
                }).orElse(false);
    }

    public static void equipEventAndSound(LivingEntity entity, ItemStack stack) {
        if (stack.getItem() instanceof ExtendedItemProperties ext) {
            SoundEvent soundevent = ext.getEquipSound(stack);
            if (!stack.isEmpty() && soundevent != null && !entity.isSpectator()) {
                entity.gameEvent(GameEvent.EQUIP);
                if (!entity.level().isClientSide)
                    ChangedSounds.broadcastSound(entity, soundevent.getLocation(), 1.0F, 1.0F);
            }
        }
    }

    public static void onBrokenAccessory(LivingEntity livingEntity, AccessorySlotType slotType) {
        if (livingEntity.level().isClientSide)
            return;

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AccessoryEventPacket(livingEntity.getId(), slotType, 1));
    }

    public static void onInteractAccessory(LivingEntity livingEntity, AccessorySlotType slotType) {
        if (livingEntity.level().isClientSide) {
            Changed.PACKET_HANDLER.sendToServer(
                    new AccessoryEventPacket(livingEntity.getId(), slotType, 2));
            return;
        }

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AccessoryEventPacket(livingEntity.getId(), slotType, 2));
    }

    public boolean isItemAllowedWithOthers(AccessorySlotType slotType, ItemStack stack) {
        if (!slotType.canHoldItem(stack, this.owner))
            return false;

        if (stack.isEmpty())
            return true;

        return ChangedRegistry.ACCESSORY_SLOTS.get().getValues().stream().filter(otherSlotType -> otherSlotType != slotType)
                .allMatch(otherSlotType -> {
                    final var otherStack = this.getItem(otherSlotType).orElse(ItemStack.EMPTY);
                    if (otherStack.isEmpty())
                        return true;

                    if (stack.getItem() instanceof AccessoryItem accessoryItem &&
                        (!accessoryItem.allowedWith(stack, otherStack, this.owner, slotType, otherSlotType) ||
                                accessoryItem.shouldDisableSlot(new AccessorySlotContext<>(this.owner, slotType, stack), otherSlotType)))
                        return false;
                    if (otherStack.getItem() instanceof AccessoryItem accessoryItem &&
                            (!accessoryItem.allowedWith(otherStack, stack, this.owner, otherSlotType, slotType) ||
                                    accessoryItem.shouldDisableSlot(new AccessorySlotContext<>(this.owner, otherSlotType, otherStack), slotType)))
                        return false;

                    return true;
                });
    }

    /**
     * Call when the slot carrier may change configuration, causing
     * @param allowedSlots test for which slots may stay
     * @param removed consumer for itemStacks that are no longer valid
     */
    public boolean initialize(Predicate<AccessorySlotType> allowedSlots, Consumer<ItemStack> removed) {
        AtomicBoolean stateChanged = new AtomicBoolean(!invalidItems.isEmpty());

        invalidItems.forEach(removed);
        invalidItems.clear();

        for (var slotType : items.keySet().stream().filter(allowedSlots.negate()).collect(Collectors.toSet())) {
            removed.accept(items.get(slotType));
            items.remove(slotType);
            stateChanged.set(true);
        }

        ChangedRegistry.ACCESSORY_SLOTS.get().getValues().stream()
                .filter(allowedSlots)
                .forEach(slotType -> items.computeIfAbsent(slotType, x -> {
                    stateChanged.set(true);
                    return ItemStack.EMPTY;
                }));

        items.entrySet().stream().filter(entry -> !isItemAllowedWithOthers(entry.getKey(), entry.getValue()))
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .forEach(slotType -> {
                    removed.accept(items.get(slotType));
                    items.put(slotType, ItemStack.EMPTY);
                    stateChanged.set(true);
                });

        orderedSlots.clear();
        return stateChanged.getAcquire();
    }

    public void tick() {
        for (var entry : items.entrySet()) {
            if (entry.getValue().getItem() instanceof AccessoryItem accessoryItem) {
                accessoryItem.accessoryTick(AccessorySlotContext.of(this.owner, entry.getKey()));
            }
        }
    }

    public void onEntitySwing(InteractionHand hand) {
        for (var entry : items.entrySet()) {
            if (entry.getValue().getItem() instanceof AccessoryItem accessoryItem) {
                accessoryItem.accessorySwing(AccessorySlotContext.of(this.owner, entry.getKey()), hand);
            }
        }
    }

    public void onEntityAttack(InteractionHand hand, Entity target) {
        for (var entry : items.entrySet()) {
            if (entry.getValue().getItem() instanceof AccessoryItem accessoryItem) {
                accessoryItem.accessoryAttack(AccessorySlotContext.of(this.owner, entry.getKey()), hand, target);
            }
        }
    }

    public void onEntityDamage(DamageSource source, float amount) {
        for (var entry : items.entrySet()) {
            if (entry.getValue().getItem() instanceof AccessoryItem accessoryItem) {
                accessoryItem.accessoryDamaged(AccessorySlotContext.of(this.owner, entry.getKey()), source, amount);
            }
        }
    }

    public boolean moveToSlot(AccessorySlotType slot, ItemStack stack) {
        if (!items.containsKey(slot))
            return false;
        if (!isItemAllowedWithOthers(slot, stack))
            return false;

        ItemStack oldStack = items.get(slot);
        if (oldStack.isEmpty()) {
            items.put(slot, stack.copy());
            stack.setCount(0);
        } else if (ItemStack.isSameItemSameTags(stack, oldStack)) {
            int maxMove = oldStack.getMaxStackSize() - oldStack.getCount();
            int toMove = Math.min(stack.getCount(), maxMove);
            oldStack.grow(toMove);
            stack.shrink(toMove);
        }

        return stack.isEmpty();
    }

    public void getAttributeModifiers(Consumer<Multimap<Attribute, AttributeModifier>> modifierConsumer) {
        forEachSlot((slotType, stack) -> modifierConsumer.accept(stack.getAttributeModifiers(slotType.getEquivalentSlot())));
    }

    public boolean quickMoveStack(ItemStack stack) {
        for (var slot : items.keySet()) {
            if (moveToSlot(slot, stack))
                return true;
        }

        return stack.isEmpty();
    }

    public boolean hasSlot(AccessorySlotType slotType) {
        return items.containsKey(slotType);
    }

    public void forEachSlot(BiConsumer<AccessorySlotType, ItemStack> consumer) {
        for (var entry : items.entrySet())
            consumer.accept(entry.getKey(), entry.getValue());
    }

    public void forEachItem(Consumer<ItemStack> consumer) {
        for (var stack : items.values())
            if (!stack.isEmpty())
                consumer.accept(stack);
    }

    public Stream<ItemStack> getItems() {
        return items.values().stream().filter(stack -> !stack.isEmpty());
    }

    public Stream<AccessorySlotType> getSlotTypes() {
        return items.keySet().stream();
    }

    public @NotNull Optional<ItemStack> getItem(AccessorySlotType slotType) {
        if (items.containsKey(slotType))
            return Optional.of(items.get(slotType));
        return Optional.empty();
    }

    public ItemStack getLastItem(AccessorySlotType slotType) {
        return Objects.requireNonNullElse(lastItems.get(slotType), ItemStack.EMPTY);
    }

    public void setLastItem(AccessorySlotType slotType, ItemStack stack) {
        lastItems.put(slotType, stack);
    }

    public Set<AccessorySlotType> getAccessorySlotsForItem(ItemStack stack) {
        return items.keySet().stream().filter(slotType -> slotType.canHoldItem(stack, owner)).collect(Collectors.toSet());
    }

    public <T> Stream<T> getMergedStream(BiFunction<AccessorySlotType, ItemStack, T> mapper) {
        return items.entrySet().stream().mapMulti((entry, sink) -> sink.accept(mapper.apply(entry.getKey(), entry.getValue())));
    }

    public boolean anyItemMatches(Predicate<ItemStack> predicate) {
        return getItems().anyMatch(predicate);
    }

    private void emptySlots() {
        items.keySet().forEach(slotType -> items.put(slotType, ItemStack.EMPTY));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        forEachSlot((slotType, stack) -> tag.put(ChangedRegistry.ACCESSORY_SLOTS.getKey(slotType).toString(), stack.serializeNBT()));
        return tag;
    }

    public void load(CompoundTag tag) {
        this.emptySlots();
        tag.getAllKeys().forEach(key -> {
            ResourceLocation id = ResourceLocation.parse(key);
            ItemStack value = ItemStack.of(tag.getCompound(key));
            if (!ChangedRegistry.ACCESSORY_SLOTS.get().containsKey(id)) {
                invalidItems.add(value);
                LOGGER.warn("Could not find slot of type {}", id);
                return;
            }

            AccessorySlotType slotType = ChangedRegistry.ACCESSORY_SLOTS.get().getValue(ResourceLocation.parse(key));
            items.put(slotType, value);
            orderedSlots.clear();
        });
    }

    public void writeNetwork(FriendlyByteBuf buffer) {
        buffer.writeMap(items,
                ChangedRegistry.ACCESSORY_SLOTS::writeRegistryObject,
                (byteBuf, stack) -> byteBuf.writeNbt(stack.serializeNBT()));
    }

    public void readNetwork(FriendlyByteBuf buffer) {
        this.emptySlots();
        items.putAll(buffer.readMap(
                ChangedRegistry.ACCESSORY_SLOTS::readRegistryObject,
                byteBuf -> ItemStack.of(byteBuf.readAnySizeNbt())
        ));
    }

    public void setAll(AccessorySlots other, boolean empty) {
        if (empty)
            items.clear();
        items.putAll(other.items);
    }

    public static Consumer<ItemStack> defaultInvalidHandler(LivingEntity entity) {
        if (entity instanceof Player player)
            return stack -> ItemHandlerHelper.giveItemToPlayer(player, stack);
        else
            return dropItemHandler(entity);
    }

    public static Consumer<ItemStack> dropItemHandler(LivingEntity entity) {
        return stack -> {
            if (stack.isEmpty()) return;

            ItemEntity itemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY() + 0.5, entity.getZ(), stack);
            itemEntity.setPickUpDelay(40);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

            entity.level().addFreshEntity(itemEntity);
        };
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.values().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int id) {
        // Default to empty stack, while GUI re-evaluates
        return Objects.requireNonNullElse(items.get(getSlotTypeByIndex(id)), ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int id, int count) {
        final AccessorySlotType slotType = getSlotTypeByIndex(id);
        return slotType != null && !items.get(slotType).isEmpty() && count > 0 ? items.get(slotType).split(count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int id) {
        final var slotType = getSlotTypeByIndex(id);
        final var taken = items.get(slotType);
        if (taken.isEmpty())
            return ItemStack.EMPTY;

        items.put(slotType, ItemStack.EMPTY);
        return taken;
    }

    public void setItem(AccessorySlotType slotType, ItemStack stack) {
        if (items.keySet().contains(slotType))
            items.put(slotType, stack);
    }

    @Override
    public void setItem(int id, ItemStack stack) {
        items.put(getSlotTypeByIndex(id), stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        if (this.owner == null || this.owner.isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this.owner) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    public void replaceWith(@Nullable AccessorySlots other) {
        this.invalidItems.addAll(this.items.values());
        this.items.clear();

        if (other != null)
            this.items.putAll(other.items);
    }

    public void dropAll(Consumer<ItemStack> consumer) {
        this.items.entrySet().stream().filter(entry -> {
            final var event = new DropItemEvent(this.owner, entry.getKey(), entry.getValue());
            ChangedCompatibility.shouldAccessoryDropOnDeath(event);
            Changed.postModEvent(event);
            return event.shouldDrop();
        }).forEach(entry -> {
            consumer.accept(entry.getValue());
            entry.setValue(ItemStack.EMPTY);
        });
    }

    public boolean hasSpaceFor(ItemStack stack) {
        for (var slot : items.keySet()) {
            if (!isItemAllowedWithOthers(slot, stack))
                continue;

            ItemStack oldStack = items.get(slot);
            if (oldStack.isEmpty()) {
                return true;
            } else if (ItemStack.isSameItemSameTags(stack, oldStack)) {
                int maxMove = oldStack.getMaxStackSize() - oldStack.getCount();
                int toMove = Math.min(stack.getCount(), maxMove);

                if (toMove > 0)
                    return true;
            }
        }

        return false;
    }

    public static class DropItemEvent extends Event {
        protected final LivingEntity entity;
        protected final AccessorySlotType slot;
        protected final ItemStack stack;
        protected boolean doDrop = true;

        public DropItemEvent(LivingEntity entity, AccessorySlotType slot, ItemStack stack) {
            this.entity = entity;
            this.slot = slot;
            this.stack = stack;
        }

        public LivingEntity getEntity() {
            return entity;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void keepItem() {
            this.doDrop = false;
        }

        public void dropItem() {
            this.doDrop = true;
        }

        public boolean shouldDrop() {
            return doDrop;
        }
    }
}
