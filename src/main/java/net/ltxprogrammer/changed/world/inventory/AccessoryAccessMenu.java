package net.ltxprogrammer.changed.world.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccessoryAccessMenu extends AbstractContainerMenu {
    static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public final Player owner;
    public final Inventory inventory;
    public final AccessorySlots accessorySlots;

    private ImmutableList<AccessorySlotType> builtSlots;
    private final Map<Integer, Slot> customSlots = new HashMap<>();

    private AccessoryAccessMenu(int id, Player owner, List<AccessorySlotType> slotTypes) {
        super(ChangedMenus.ACCESSORY_ACCESS.get(), id);
        this.owner = owner;
        this.inventory = owner.getInventory();
        this.accessorySlots = AccessorySlots.getForEntity(owner).orElseGet(AccessorySlots::new);
        this.createSlots(inventory, slotTypes);

        if (owner.containerMenu != null) {
            this.setCarried(owner.containerMenu.getCarried());
            owner.containerMenu.setCarried(ItemStack.EMPTY);
        }
    }

    public AccessoryAccessMenu(int id, Player owner) {
        this(id, owner, AccessorySlots.getForEntity(owner).orElseGet(AccessorySlots::new).getOrderedSlots());
    }

    public static void openForPlayer(ServerPlayer player) {
        final var registry = ChangedRegistry.ACCESSORY_SLOTS.get();
        final var slots = AccessorySlots.getForEntity(player).orElseGet(AccessorySlots::new);

        NetworkHooks.openScreen(player,
                new SimpleMenuProvider((id, inv, accessor) -> new AccessoryAccessMenu(id, accessor, slots.getOrderedSlots()), Component.empty()),
                extra -> {
                    extra.writeCollection(slots.getOrderedSlots(), ChangedRegistry.ACCESSORY_SLOTS::writeRegistryObject);
                });
    }

    private static List<AccessorySlotType> readSlotTypes(@Nullable FriendlyByteBuf buffer) {
        if (buffer == null)
            return List.of();
        return buffer.readList(ChangedRegistry.ACCESSORY_SLOTS::readRegistryObject);
    }

    public AccessoryAccessMenu(int id, Inventory inventory, FriendlyByteBuf extra) {
        this(id, inventory.player, readSlotTypes(extra));
    }

    protected void makeAccessorySlots(List<AccessorySlotType> slotTypes) {
        var builder = ImmutableList.<AccessorySlotType>builder();

        for (int si = 0; si < slotTypes.size(); ++si) {
            final AccessorySlotType slotType = slotTypes.get(si);
            this.customSlots.put(si, this.addSlot(new Slot(this.accessorySlots, si, 77 + ((si % 5) * 18), 8 + ((si / 5) * 18)) {
                @Override
                public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, slotType.getNoItemIcon());
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return super.mayPlace(stack) && AccessoryAccessMenu.this.accessorySlots.isItemAllowedWithOthers(slotType, stack);
                }
            }));

            builder.add(slotType);
        }

        builtSlots = builder.build();
    }

    protected void createSlots(Inventory inv, List<AccessorySlotType> slotTypes) {
        for (int si = 0; si < 4; ++si) {
            final EquipmentSlot equipmentSlot = SLOT_IDS[si];
            this.addSlot(new Slot(inv, 39 - si, 8, 8 + (si * 18)) {
                public int getMaxStackSize() {
                    return 1;
                }

                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentSlot, owner);
                }

                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()]);
                }
            });
        }

        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 8 + si * 18, 142));

        this.makeAccessorySlots(slotTypes);
    }

    @Override
    public boolean stillValid(Player player) {
        final var currentSlots = accessorySlots.getSlotTypes().collect(Collectors.toSet());
        return currentSlots.containsAll(builtSlots) && builtSlots.containsAll(currentSlots);
    }

    public List<AccessorySlotType> getBuiltSlots() {
        return builtSlots;
    }

    public EquipmentSlot denyInvalidArmorSlot(ItemStack itemStack) {
        var slot = Mob.getEquipmentSlotForItem(itemStack);
        if (slot.getType() != EquipmentSlot.Type.ARMOR)
            return slot;

        return ProcessTransfur.ifPlayerTransfurred(this.owner, variant -> {
            return variant.canWear(this.owner, itemStack, slot) ? slot : EquipmentSlot.MAINHAND;
        }, () -> slot);
    }

    /**
     * Moves the given stack into the first available slots in the range
     * @param stack item stack
     * @param slotRangeStart range start (inclusive)
     * @param slotRangeEnd range end (exclusive)
     * @param reversed iterate in reverse, end to start
     * @return true if the stack was moved partially, or completely. false if no items were moved
     */
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int slotRangeStart, int slotRangeEnd, boolean reversed) {
        return super.moveItemStackTo(stack, slotRangeStart, slotRangeEnd, reversed);
    }

    // 0-3 -> armor, 4-30 -> hotbar, 31->39 -> inventory, 40+ -> accessories
    public ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot != null && slot.hasItem()) {
            ItemStack oldStack = slot.getItem();
            stack = oldStack.copy();
            EquipmentSlot equipmentslot = denyInvalidArmorSlot(stack);
            Set<Integer> accessorySlotIndices = IntStream.range(0, builtSlots.size())
                    .filter(index -> builtSlots.get(index).canHoldItem(oldStack, player)).map(i -> i + 40)
                    .collect(HashSet::new, Set::add, Set::addAll);
            if (slotId < 4) { // Move out of armor
                if (!this.moveItemStackTo(oldStack, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotId >= 40) { // Move out of accessories
                if (!this.moveItemStackTo(oldStack, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(3 - equipmentslot.getIndex()).hasItem()) { // Move to armor
                int armorSlot = 3 - equipmentslot.getIndex();
                if (!this.moveItemStackTo(oldStack, armorSlot, armorSlot + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!accessorySlotIndices.stream().map(this.slots::get).allMatch(Slot::hasItem)) { // Move to accessory
                for (Integer slotIndex : accessorySlotIndices) {
                    if (!this.slots.get(slotIndex).hasItem()) {
                        if (!this.moveItemStackTo(oldStack, slotIndex, slotIndex + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (slotId >= 4 && slotId < 30) { // Move to hotbar
                if (!this.moveItemStackTo(oldStack, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotId >= 31 && slotId < 40) { // Move from hotbar
                if (!this.moveItemStackTo(oldStack, 4, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(oldStack, 4, 30, false)) {
                return ItemStack.EMPTY;
            }

            if (oldStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (oldStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, oldStack);
            if (slotId == 0) {
                player.drop(oldStack, false);
            }
        }

        return stack;
    }

    public Slot getCustomSlot(int id) {
        return customSlots.get(id);
    }
}
