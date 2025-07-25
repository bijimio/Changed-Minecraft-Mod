package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Lifecycle;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.client.latexparticles.LatexParticleType;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.animation.AnimationEvent;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ChangedRegistry<T> implements Registry<T> {
    private static final Logger LOGGER = LogManager.getLogger(ChangedRegistry.class);

    private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
    private static final HashMap<ResourceKey<Registry<?>>, Supplier<IForgeRegistry<?>>> REGISTRY_HOLDERS = new HashMap<>();

    public static class RegistryHolder<T> implements Supplier<IForgeRegistry<T>> {
        protected final ResourceKey<Registry<T>> key;

        public RegistryHolder(ResourceKey<Registry<T>> key) {
            this.key = key;
        }

        public ResourceLocation getKey(T value) {
            return get().getKey(value);
        }

        public T getValue(ResourceLocation key) {
            return get().getValue(key);
        }

        public Set<ResourceLocation> getKeys() {
            return get().getKeys();
        }

        public int getID(T value) {
            return getRaw().getID(value);
        }

        public T getValue(int id) {
            return getRaw().getValue(id);
        }

        public void writeRegistryObject(FriendlyByteBuf buffer, T value) {
            buffer.writeInt(getRaw().getID(value));
        }

        public T readRegistryObject(FriendlyByteBuf buffer) {
            return getRaw().getValue(buffer.readInt());
        }

        @Override
        public IForgeRegistry<T> get() {
            if (REGISTRY_HOLDERS.isEmpty())
                throw new IllegalStateException("Cannot access registries before creation");
            return (IForgeRegistry<T>) REGISTRY_HOLDERS.get(key).get();
        }

        public ForgeRegistry<T> getRaw() {
            if (REGISTRY_HOLDERS.isEmpty())
                throw new IllegalStateException("Cannot access registries before creation");
            return (ForgeRegistry<T>) REGISTRY_HOLDERS.get(key).get();
        }

        public DeferredRegister<T> createDeferred(String modId) {
            return DeferredRegister.create(key, modId);
        }
    }

    // TODO rename registeries to be plural, and have modern names
    public static final RegistryHolder<TransfurVariant<?>> TRANSFUR_VARIANT = new RegistryHolder<TransfurVariant<?>>(registryKey("latex_variant"));
    public static final RegistryHolder<AbstractAbility<?>> ABILITY = new RegistryHolder<AbstractAbility<?>>(registryKey("ability"));
    public static final RegistryHolder<HairStyle> HAIR_STYLE = new RegistryHolder<HairStyle>(registryKey("hair_style"));
    public static final RegistryHolder<PlayerMover<?>> PLAYER_MOVER = new RegistryHolder<PlayerMover<?>>(registryKey("player_mover"));
    public static final RegistryHolder<LatexParticleType<?>> LATEX_PARTICLE_TYPE = new RegistryHolder<LatexParticleType<?>>(registryKey("latex_particle_type"));
    public static final RegistryHolder<AnimationEvent<?>> ANIMATION_EVENTS = new RegistryHolder<AnimationEvent<?>>(registryKey("animation_events"));
    public static final RegistryHolder<AccessorySlotType> ACCESSORY_SLOTS = new RegistryHolder<AccessorySlotType>(registryKey("accessory_slots"));
    public static final RegistryHolder<LatexType> LATEX_TYPE = new RegistryHolder<>(registryKey("latex_type"));

    private static class ClearableObjectIntIdentityMap<I> extends IdMapper<I> {
        void clear()
        {
            this.tToId.clear();
            this.idToT.clear();
            this.nextId = 0;
        }

        @SuppressWarnings("unused")
        void remove(I key)
        {
            boolean hadId = this.tToId.containsKey(key);
            int prev = this.tToId.removeInt(key);
            if (hadId) {
                this.idToT.set(prev, null);
            }
        }
    }

    @SubscribeEvent
    public static void onCreateRegistries(NewRegistryEvent event) {
        createRegistry(event, TRANSFUR_VARIANT.key, builder -> {
            builder.hasTags();
            builder.missing((key, network) -> ChangedTransfurVariants.FALLBACK_VARIANT.get());
        }, null);
        createRegistry(event, ABILITY.key);
        createRegistry(event, HAIR_STYLE.key, builder -> {
            builder.missing((key, network) -> HairStyle.BALD.get());
        }, null);
        createRegistry(event, PLAYER_MOVER.key, builder -> {
            builder.missing((key, network) -> PlayerMover.DEFAULT_MOVER.get());
        }, null);
        createRegistry(event, LATEX_PARTICLE_TYPE.key);
        createRegistry(event, ANIMATION_EVENTS.key);
        createRegistry(event, ACCESSORY_SLOTS.key);
        createRegistry(event, LATEX_TYPE.key, builder -> {
            builder.hasTags();
            builder.missing((key, network) -> ChangedLatexTypes.NONE.get());
            builder.onClear((owner, stage) -> {
                owner.getSlaveMap(ChangedLatexTypes.LATEXCOVERSTATE_TO_ID, ClearableObjectIntIdentityMap.class).clear();
            });
            builder.onCreate((owner, stage) -> {
                final ClearableObjectIntIdentityMap<LatexCoverState> idMap = new ClearableObjectIntIdentityMap<>() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public int getId(LatexCoverState key) {
                        return this.tToId.containsKey(key) ? this.tToId.getInt(key) : -1;
                    }
                };
                owner.setSlaveMap(ChangedLatexTypes.LATEXCOVERSTATE_TO_ID, idMap);
            });
            builder.onBake((owner, stage) -> {
                ClearableObjectIntIdentityMap<LatexCoverState> blockstateMap = owner.getSlaveMap(ChangedLatexTypes.LATEXCOVERSTATE_TO_ID, ClearableObjectIntIdentityMap.class);

                for (LatexType type : owner) {
                    for (LatexCoverState state : type.getStateDefinition().getPossibleStates()) {
                        blockstateMap.add(state);
                        state.initCache();
                    }
                }
                DebugLevelSource.initValidStates();
            });
        }, null);
    }

    private static <T> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key) {
        createRegistry(event, key, null, null);
    }

    private static <T> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key,
                                                                          @Nullable Consumer<RegistryBuilder<T>> additionalBuilder,
                                                                          @Nullable Consumer<IForgeRegistry<T>> onFill) {
        var builder = makeRegistry(key);
        if (additionalBuilder != null)
            additionalBuilder.accept(builder);
        Supplier<IForgeRegistry<T>> holder = event.create(builder, onFill);
        REGISTRY_HOLDERS.put((ResourceKey)key, () -> (ForgeRegistry<?>)holder.get());
        LOGGER.info("Created registry {}", key);
    }

    static <T> Class<T> c(Class<?> cls) { return (Class<T>)cls; }
    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key) {
        return RegistryBuilder.<T>of(key.location()).setMaxID(MAX_VARINT);
    }

    private ChangedRegistry() {}

    private static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(Changed.modResource(name));
    }
}
