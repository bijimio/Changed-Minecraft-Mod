package net.ltxprogrammer.changed.world.biome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record ChangedSpawnBiomeModifier() implements BiomeModifier {

    public static final Codec<ChangedSpawnBiomeModifier> CODEC = Codec.unit(new ChangedSpawnBiomeModifier());

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        MinecraftForge.EVENT_BUS.post(new AddChangedEntitiesSpawnEvent(biome, phase, builder));
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}