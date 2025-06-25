package net.ltxprogrammer.changed.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;

public class SimpleSpawnEggItem extends ForgeSpawnEggItem {
    private static final String baseText = "item.changed.simple_spawn_egg";
    private static final Component AWAITING = Component.translatable("item.changed.simple_spawn_egg.loading");
    private final RegistryObject<? extends EntityType<? extends Mob>> type;
    private Component name = AWAITING;

    public SimpleSpawnEggItem(RegistryObject<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
        this.type = type;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (name == AWAITING && type.isPresent())
            name = Component.translatable(baseText, type.get().getDescription());
        return name;
    }
}