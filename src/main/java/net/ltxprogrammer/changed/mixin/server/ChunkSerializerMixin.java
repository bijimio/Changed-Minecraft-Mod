package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelChunkSectionExtension;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Shadow
    private static void logErrors(ChunkPos p_188240_, int p_188241_, String p_188242_) {}

    @Unique
    private static final Codec<PalettedContainer<LatexCoverState>> LATEX_STATE_CODEC = PalettedContainer.codecRW(ChangedLatexTypes.getLatexCoverStateIDMap(), LatexCoverState.CODEC.getOrThrow(), PalettedContainer.Strategy.SECTION_STATES, ChangedLatexTypes.NONE.get().defaultCoverState());

    @Unique
    private static CompoundTag cachedSectionTag = null;

    @WrapOperation(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;getCompound(I)Lnet/minecraft/nbt/CompoundTag;", ordinal = 0))
    private static CompoundTag captureNextSectionTag(ListTag instance, int index, Operation<CompoundTag> original) {
        cachedSectionTag = original.call(instance, index);
        return cachedSectionTag;
    }

    @WrapOperation(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"))
    private static void readChangedChunkLayers(PoiManager instance, SectionPos position, LevelChunkSection section,
                                               Operation<Void> original,
                                               @Local(argsOnly = true) ChunkPos chunkpos) {
        PalettedContainer<LatexCoverState> container;
        if (cachedSectionTag != null && cachedSectionTag.contains("latex_cover_states", 10)) {
            container = LATEX_STATE_CODEC.parse(NbtOps.INSTANCE, cachedSectionTag.getCompound("latex_cover_states")).promotePartial((error) -> {
                logErrors(chunkpos, cachedSectionTag.getByte("Y"), error);
            }).getOrThrow(false, LOGGER::error);
        } else {
            container = new PalettedContainer<>(ChangedLatexTypes.getLatexCoverStateIDMap(), ChangedLatexTypes.NONE.get().defaultCoverState(), PalettedContainer.Strategy.SECTION_STATES);
        }

        ((LevelChunkSectionExtension)section).acceptLatexStates(container);
        ((LevelChunkSectionExtension)section).recalcLatexCoverCounts();

        original.call(instance, position, section);
    }

    @WrapOperation(method = "write", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;",
            ordinal = 2))
    private static Tag writeChangedChunkLayers(CompoundTag tag, String name, Tag biomeTag,
                                               Operation<Tag> original,
                                               @Local LevelChunkSection section) {
        final Tag result = original.call(tag, name, biomeTag);

        tag.put("latex_cover_states", LATEX_STATE_CODEC.encodeStart(NbtOps.INSTANCE, ((LevelChunkSectionExtension)section).getLatexStates()).getOrThrow(false, LOGGER::error));

        return result;
    }
}
