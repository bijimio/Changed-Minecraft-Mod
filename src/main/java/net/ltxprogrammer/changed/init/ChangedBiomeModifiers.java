package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.biome.ChangedSpawnBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Changed.MODID);

    //Todo maybe make that as a tag list so it can be more convenient?

    public static final RegistryObject<Codec<? extends BiomeModifier>> MOB_SPAWN_CODEC =
            BIOME_MODIFIERS.register("add_spawns", () -> ChangedSpawnBiomeModifier.CODEC);

    private static void register(String name, Codec<? extends BiomeModifier> codec){
        BIOME_MODIFIERS.register(name, () -> codec);
    }
}
