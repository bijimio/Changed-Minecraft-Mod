package net.ltxprogrammer.changed.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class AddChangedEntitiesSpawnEvent extends Event {


    private final Holder<Biome> biome;
    private final ModifiableBiomeInfo.BiomeInfo.Builder builder;
    private final BiomeModifier.Phase biomeModifierPhase;

    public AddChangedEntitiesSpawnEvent(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder){
        this.biome = biome;
        this.biomeModifierPhase = phase;
        this.builder = builder;
    }

    public BiomeModifier.Phase getBiomeModifierPhase() {
        return biomeModifierPhase;
    }

    @Nullable
    public ResourceKey<Biome> getName(){
        return this.getBiome().unwrapKey().orElse(null);
    }

    public Holder<Biome> getBiome() {
        return biome;
    }

    public ModifiableBiomeInfo.BiomeInfo.Builder getBuilder() {
        return builder;
    }

    public MobSpawnSettingsBuilder getSpawns() {
        return this.builder.getMobSpawnSettings();
    }
}
