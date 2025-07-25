package net.ltxprogrammer.changed.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface SpecializedItemRendering {
    static boolean isGUI(ItemDisplayContext type) {
        return type == ItemDisplayContext.GUI || type == ItemDisplayContext.GROUND || type == ItemDisplayContext.FIXED;
    }

    @Nullable @Deprecated
    default ModelResourceLocation getEmissiveModelLocation(ItemStack itemStack, ItemDisplayContext type) { return null; }
    ModelResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type);
    void loadSpecialModels(Consumer<ResourceLocation> loader);

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    class Event {
        @SubscribeEvent
        public static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
            ForgeRegistries.ITEMS.forEach(item -> {
                if (item instanceof SpecializedItemRendering specialized)
                    specialized.loadSpecialModels(event::register);
            });
        }
    }
}
