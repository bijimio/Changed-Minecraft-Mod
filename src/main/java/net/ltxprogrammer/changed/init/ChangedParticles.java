package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.effect.particle.*;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Changed.MODID);

    public static final RegistryObject<ParticleType<ColoredParticleOption>> DRIPPING_LATEX = register("dripping_latex",
            ColoredParticleOption.DESERIALIZER, ColoredParticleOption::codec);
    public static final RegistryObject<ParticleType<ColoredParticleOption>> GAS = register("gas",
            ColoredParticleOption.DESERIALIZER, ColoredParticleOption::codec);
    public static final RegistryObject<ParticleType<EmoteParticleOption>> EMOTE = register("emote",
            EmoteParticleOption.DESERIALIZER, EmoteParticleOption::codec);
    public static final RegistryObject<SimpleParticleType> TSC_SWEEP_ATTACK = REGISTRY.register("tsc_sweep_attack",
            () -> new SimpleParticleType(false));

    public static ColoredParticleOption drippingLatex(Color3 color) {
        return new ColoredParticleOption(DRIPPING_LATEX.get(), color);
    }

    public static ColoredParticleOption gas(Color3 color) {
        return new ColoredParticleOption(GAS.get(), color);
    }

    public static EmoteParticleOption emote(Entity entity, Emote emote) {
        return new EmoteParticleOption(EMOTE.get(), emote, entity);
    }

    private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name, ParticleOptions.Deserializer<T> dec, final Function<ParticleType<T>, Codec<T>> fn) {
        var type = new ParticleType<T>(false, dec) {
            public Codec<T> codec() {
                return fn.apply(this);
            }
        };

        return REGISTRY.register(name, () -> new ParticleType<T>(false, dec) {
            public Codec<T> codec() {
                return fn.apply(this);
            }
        });
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(DRIPPING_LATEX.get(), LatexDripParticle.Provider::new);
        event.registerSpriteSet(GAS.get(), GasParticle.Provider::new);
        event.registerSpriteSet(EMOTE.get(), EmoteParticle.Provider::new);
        event.registerSpriteSet(TSC_SWEEP_ATTACK.get(), TscSweepParticle.Provider::new);
    }
}
