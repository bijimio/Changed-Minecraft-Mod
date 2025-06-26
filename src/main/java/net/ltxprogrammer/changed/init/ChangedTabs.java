package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.item.LatexRecordItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Predicate;

public class ChangedTabs {
    private static CreativeModeTab.Builder makeTab(String id, CreativeModeTab.DisplayItemsGenerator generator) {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + id))
                .displayItems(generator);
    }

    public static DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Changed.MODID);

    private static Predicate<TransfurVariant<?>> CHANGED_ONLY = variant -> variant.getFormId().getNamespace().equals(Changed.MODID);

    private static RegistryObject<CreativeModeTab> register(String id, Function<CreativeModeTab.Builder, CreativeModeTab> finalizer) {
        return REGISTRY.register(id, () -> finalizer.apply(
                CreativeModeTab.builder().title(Component.translatable("itemGroup.tab_changed_" + id))
        ));
    }

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_BLOCKS = register("blocks", builder ->
            builder.icon(() -> new ItemStack(ChangedBlocks.WALL_LIGHTRED_STRIPED.get()))
                    .displayItems((params, output) -> {
                        // TODO
                    }).build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_ITEMS = register("items", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.LATEX_BASE.get()))
                    .displayItems((params, output) -> {
                        // TODO

                        ChangedItems.DARK_LATEX_MASK.get().fillItemList(CHANGED_ONLY, params, output);
                        ChangedItems.LATEX_SYRINGE.get().fillItemList(CHANGED_ONLY, params, output);
                        ChangedItems.LATEX_FLASK.get().fillItemList(CHANGED_ONLY, params, output);
                        ChangedItems.LATEX_TIPPED_ARROW.get().fillItemList(CHANGED_ONLY, params, output);
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_ENTITIES = register("entities", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.DARK_LATEX_MASK.get()))
                    .displayItems((params, output) -> {
                        ChangedEntities.SPAWN_EGGS.values().stream()
                                .map(RegistryObject::get)
                                .forEach(output::accept);
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_COMBAT = register("combat", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.TSC_BATON.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.TSC_BATON.get());
                        output.accept(ChangedItems.TSC_STAFF.get());
                        output.accept(ChangedItems.TSC_SHIELD.get());

                        output.accept(ChangedItems.LEATHER_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.LEATHER_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.LEATHER_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.LEATHER_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.CHAINMAIL_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.CHAINMAIL_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.CHAINMAIL_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.CHAINMAIL_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.IRON_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.IRON_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.IRON_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.IRON_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.GOLDEN_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.GOLDEN_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.GOLDEN_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.GOLDEN_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.DIAMOND_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.DIAMOND_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.DIAMOND_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.DIAMOND_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.NETHERITE_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.NETHERITE_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.NETHERITE_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.NETHERITE_QUADRUPEDAL_BOOTS.get());
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_CLOTHING = register("clothing", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.BENIGN_SHORTS.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.BENIGN_SHORTS.get());
                        output.accept(ChangedItems.PINK_SHORTS.get());
                        output.accept(ChangedItems.BLACK_TSHIRT.get());
                        output.accept(ChangedItems.SPORTS_BRA.get());
                        output.accept(ChangedItems.LAB_COAT.get());
                        output.accept(ChangedItems.WETSUIT.get());
                        output.accept(ChangedItems.NITRILE_GLOVES.get());
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_MUSIC = register("music", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.PURO_THE_BLACK_GOO_RECORD.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.BLACK_GOO_ZONE_RECORD.get());
                        output.accept(ChangedItems.CRYSTAL_ZONE_RECORD.get());
                        output.accept(ChangedItems.GAS_ROOM_RECORD.get());
                        output.accept(ChangedItems.LABORATORY_RECORD.get());
                        output.accept(ChangedItems.OUTSIDE_THE_TOWER_RECORD.get());
                        output.accept(ChangedItems.PURO_THE_BLACK_GOO_RECORD.get());
                        output.accept(ChangedItems.PUROS_HOME_RECORD.get());
                        output.accept(ChangedItems.THE_LIBRARY_RECORD.get());
                        output.accept(ChangedItems.THE_LION_CHASE_RECORD.get());
                        output.accept(ChangedItems.THE_SCARLET_CRYSTAL_MINE_RECORD.get());
                        output.accept(ChangedItems.THE_SHARK_RECORD.get());
                        output.accept(ChangedItems.THE_SQUID_DOG_RECORD.get());
                        output.accept(ChangedItems.THE_WHITE_GOO_JUNGLE_RECORD.get());
                        output.accept(ChangedItems.THE_WHITE_TAIL_CHASE_PART_1.get());
                        output.accept(ChangedItems.THE_WHITE_TAIL_CHASE_PART_2.get());
                        output.accept(ChangedItems.VENT_PIPE_RECORD.get());
                    })
                    .build());
}
