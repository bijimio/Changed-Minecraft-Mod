package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ChangedBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Changed.MODID);

    //Todo maybe make that as a tag list so it can be more convenient?


    private static void register(String name, Codec<? extends BiomeModifier> codec){
        BIOME_MODIFIERS.register(name, () -> codec);
    }
}
