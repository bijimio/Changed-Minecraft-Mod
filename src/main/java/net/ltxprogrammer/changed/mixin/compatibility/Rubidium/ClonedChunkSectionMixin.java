package net.ltxprogrammer.changed.mixin.compatibility.Rubidium;

import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import me.jellysquid.mods.sodium.client.world.cloned.PalettedContainerAccessor;
import me.jellysquid.mods.sodium.client.world.cloned.palette.ClonedPalette;
import me.jellysquid.mods.sodium.client.world.cloned.palette.ClonedPaletteFallback;
import me.jellysquid.mods.sodium.client.world.cloned.palette.ClonedPalleteArray;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.rubidium.ClonedChunkSectionExtension;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelChunkSectionExtension;
import net.minecraft.core.SectionPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClonedChunkSection.class, remap = false)
@RequiredMods("rubidium")
public abstract class ClonedChunkSectionMixin implements ClonedChunkSectionExtension {
    @Unique private SimpleBitStorage latexCoverStateData;
    @Unique private ClonedPalette<LatexCoverState> latexCoverStatePalette;

    @Inject(method = "reset", at = @At("TAIL"))
    private void resetLatexCover(SectionPos pos, CallbackInfo ci) {
        this.latexCoverStateData = null;
        this.latexCoverStatePalette = null;
    }

    @Unique
    private static SimpleBitStorage copyLatexCoverData(PalettedContainer.Data<LatexCoverState> container) {
        BitStorage storage = container.storage();
        long[] data = storage.getRaw();
        int bits = container.configuration().bits();
        return bits == 0 ? new SimpleBitStorage(1, storage.getSize()) : new SimpleBitStorage(bits, storage.getSize(), (long[])(data).clone());
    }

    @Unique
    private static ClonedPalette<LatexCoverState> copyPalette(PalettedContainer.Data<LatexCoverState> container) {
        Palette<LatexCoverState> palette = container.palette();
        if (palette instanceof GlobalPalette) {
            return new ClonedPaletteFallback<>(ChangedLatexTypes.getLatexCoverStateIDMap());
        } else {
            LatexCoverState[] array = new LatexCoverState[container.palette().getSize()];

            for(int i = 0; i < array.length; ++i) {
                array[i] = palette.valueFor(i);
            }

            return new ClonedPalleteArray<>(array);
        }
    }

    @Inject(method = "copyBlockData(Lnet/minecraft/world/level/chunk/LevelChunkSection;)V", at = @At("TAIL"))
    private void copyLatexCoverData(LevelChunkSection section, CallbackInfo ci) {
        PalettedContainer.Data<LatexCoverState> container = PalettedContainerAccessor.getData(((LevelChunkSectionExtension)section).getLatexStates());
        this.latexCoverStateData = copyLatexCoverData(container);
        this.latexCoverStatePalette = copyPalette(container);
    }

    public SimpleBitStorage getLatexCoverData() {
        return this.latexCoverStateData;
    }

    public ClonedPalette<LatexCoverState> getLatexCoverPalette() {
        return this.latexCoverStatePalette;
    }
}
