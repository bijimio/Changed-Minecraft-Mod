package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.ltxprogrammer.changed.client.WaveVisionRenderer;
import net.minecraft.Util;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(ChunkBufferBuilderPack.class)
public abstract class ChunkBufferBuilderPackMixin {
    @Unique
    private final Map<RenderType, BufferBuilder> extraBuilders = Util.make(() -> {
        if (!ChangedClient.shouldBeRenderingWaveVision())
            return null;

        final var extraLayers = Set.of(
            ChangedShaders.waveVisionResonantSolid(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL),
            ChangedShaders.waveVisionResonantCutoutMipped(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL),
            ChangedShaders.waveVisionResonantCutout(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL)
        );

        return extraLayers.stream().collect(Collectors.toMap(Function.identity(), (renderType) -> {
            return new BufferBuilder(renderType.bufferSize());
        }));
    });

    @WrapMethod(method = "builder")
    public BufferBuilder orWaveVision(RenderType renderType, Operation<BufferBuilder> original) {
        if (extraBuilders != null && extraBuilders.containsKey(renderType))
            return extraBuilders.get(renderType);
        return original.call(renderType);
    }
}
