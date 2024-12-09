package net.ltxprogrammer.changed.client.animations;

import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.ClientLivingEntityExtender;
import net.ltxprogrammer.changed.entity.animation.AnimationAssociation;
import net.ltxprogrammer.changed.entity.animation.AnimationCategory;
import net.ltxprogrammer.changed.entity.animation.AnimationEvent;
import net.ltxprogrammer.changed.entity.animation.AnimationParameters;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AnimationAssociations extends SimplePreparableReloadListener<ImmutableMultimap<ResourceLocation, AnimationAssociation>> {
    public static AnimationAssociations INSTANCE = new AnimationAssociations();

    private static ImmutableMultimap<ResourceLocation, AnimationAssociation> associations;

    private void processJSONFile(ImmutableMultimap.Builder<ResourceLocation, AnimationAssociation> map, ResourceLocation filename, JsonObject root) {
        root.entrySet().forEach(entry -> {
            final JsonObject object = entry.getValue().getAsJsonObject();
            final AnimationCategory category = AnimationCategory.fromSerial(GsonHelper.getAsString(object, "category"))
                    .getOrThrow(false, str -> {});
            object.getAsJsonArray("animations").forEach(animation -> {
                final JsonObject animationObject = animation.getAsJsonObject();
                map.put(new ResourceLocation(entry.getKey()),
                        new AnimationAssociation(category,
                                new ResourceLocation(animationObject.get("name").getAsString()),
                                animationObject.getAsJsonObject("criteria")
                        ));
            });
        });
    }

    @Override
    @NotNull
    protected ImmutableMultimap<ResourceLocation, AnimationAssociation> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        final ImmutableMultimap.Builder<ResourceLocation, AnimationAssociation> builder = ResourceUtil.processJSONFiles(new ImmutableMultimap.Builder<>(),
                resourceManager, "animations.json", this::processJSONFile,
                (exception, filename) -> Changed.LOGGER.error("Failed to load animation associations from \"{}\" : {}", filename, exception));

        return builder.build();
    }

    @Override
    protected void apply(@NotNull ImmutableMultimap<ResourceLocation, AnimationAssociation> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        associations = output;
    }

    public static <T extends AnimationParameters> void dispatchAnimation(LivingEntity livingEntity, AnimationEvent<T> event, @Nullable AnimationCategory category, @Nullable T parameters,
                                                                         List<LivingEntity> propEntities, List<ItemStack> propItems) {
        final var eventAssoc = associations.get(event.getRegistryName()).stream()
                .filter(assoc -> parameters == null || parameters.apply(assoc) != AnimationAssociation.Match.DENY)
                .toList();

        final var chosen = Util.getRandomSafe(eventAssoc, livingEntity.getRandom());
        chosen.map(AnimationAssociation::getName)
                .map(AnimationDefinitions::getAnimation)
                .map(definition -> definition.createInstance(livingEntity))
                .ifPresent(instance -> {
                    propEntities.forEach(instance::addEntity);
                    propItems.forEach(instance::addItem);

                    ((ClientLivingEntityExtender)livingEntity).addAnimation(
                        category == null ? chosen.orElseThrow().getCategory() : category,
                        instance);
                });
    }
}
