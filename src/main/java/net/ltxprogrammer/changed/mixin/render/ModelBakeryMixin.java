package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.math.Transformation;
import net.ltxprogrammer.changed.client.AbilityRenderer;
import net.ltxprogrammer.changed.client.BakeryExtender;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin implements BakeryExtender {
    @Shadow @Final private static Logger LOGGER;
    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation p_119353_, UnbakedModel p_119354_);

    @Shadow @Final private Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow public abstract UnbakedModel getModel(ResourceLocation p_119342_);
    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation p_119365_) throws IOException;

    @WrapMethod(method = "loadModel")
    public void orLoadAbilityModel(ResourceLocation modelName, Operation<Void> original) throws Exception {
        if (modelName instanceof ModelResourceLocation modelLocation && Objects.equals(modelLocation.getVariant(), "ability")) {
            ResourceLocation resourcelocation = modelName.withPrefix("ability/");
            BlockModel blockmodel = this.loadBlockModel(resourcelocation);
            this.cacheAndQueueDependencies(modelLocation, blockmodel);
            this.unbakedCache.put(resourcelocation, blockmodel);
        } else {
            original.call(modelName);
        }
    }

    @Override
    @Unique
    public void removeFromCacheIf(Predicate<Triple<ResourceLocation, Transformation, Boolean>> predicate) {
        var toRemove = bakedCache.keySet().stream().filter(predicate).toList();
        for (var triple : toRemove)
            bakedCache.remove(triple);
    }
}
