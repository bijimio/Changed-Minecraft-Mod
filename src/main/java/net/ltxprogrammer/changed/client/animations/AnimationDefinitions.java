package net.ltxprogrammer.changed.client.animations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AnimationDefinitions extends SimplePreparableReloadListener<AnimationDefinitions.GatherAnimationsEvent> {
    public static AnimationDefinitions INSTANCE = new AnimationDefinitions();

    private static ImmutableMap<ResourceLocation, AnimationDefinition> definitions;

    public static AnimationDefinition getAnimation(ResourceLocation id) {
        return definitions.get(id);
    }

    public static class GatherAnimationsEvent extends Event implements IModBusEvent {
        private final ImmutableSet<ResourceLocation> jsonDefined;
        private final HashMap<ResourceLocation, AnimationDefinition> builder;

        public enum OverridePolicy {
            YIELD_TO_JSON,
            OVERRIDE_JSON
        }

        public GatherAnimationsEvent(ImmutableSet<ResourceLocation> jsonDefined, HashMap<ResourceLocation, AnimationDefinition> builder) {
            this.jsonDefined = jsonDefined;
            this.builder = builder;
        }

        public void addAnimationDefinition(ResourceLocation id, AnimationDefinition definition) {
            this.addAnimationDefinition(id, definition, OverridePolicy.YIELD_TO_JSON);
        }

        public void addAnimationDefinition(ResourceLocation id, AnimationDefinition definition, OverridePolicy policy) {
            if (policy == OverridePolicy.YIELD_TO_JSON && jsonDefined.contains(id))
                Changed.LOGGER.debug("Animation {} from gather event was ignored by prioritizing existing JSON definition", id);
            else
                builder.put(id, definition);
        }

        private ImmutableMap<ResourceLocation, AnimationDefinition> build() {
            return ImmutableMap.copyOf(this.builder);
        }
    }

    private AnimationDefinition processJSONFile(JsonObject root) {
        return AnimationDefinition.CODEC.parse(JsonOps.INSTANCE, root)
                .getOrThrow(false, string -> {});
    }

    @Override
    @NotNull
    protected AnimationDefinitions.GatherAnimationsEvent prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        final ImmutableSet.Builder<ResourceLocation> jsonDefined = new ImmutableSet.Builder<>();
        final HashMap<ResourceLocation, AnimationDefinition> builder = ResourceUtil.processJSONResources(new HashMap<>(),
                resourceManager, "animation_definitions",
                (map, filename, id, json) -> {
                    map.put(id, processJSONFile(json));
                    jsonDefined.add(id);
                },
                (exception, filename) -> Changed.LOGGER.error("Failed to load animation definition from \"{}\" : {}", filename, exception));

        return new GatherAnimationsEvent(jsonDefined.build(), builder);
    }

    @Override
    protected void apply(@NotNull AnimationDefinitions.GatherAnimationsEvent event, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        ModLoader.get().postEvent(event);

        definitions = event.build();
    }
}
