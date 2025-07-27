package net.ltxprogrammer.changed.mixin.compatibility.Sodium;

import me.jellysquid.mods.sodium.client.world.ReadableContainerExtended;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.sodium.ClonedChunkSectionExtension;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelChunkSectionExtension;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClonedChunkSection.class, remap = false)
@RequiredMods("rubidium")
public abstract class ClonedChunkSectionMixin implements ClonedChunkSectionExtension {
    @Unique private PalettedContainerRO<LatexCoverState> latexCoverStateData;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initChanged(Level world, LevelChunk chunk, @Nullable LevelChunkSection section, SectionPos pos, CallbackInfo ci) {
        PalettedContainerRO<LatexCoverState> latexCoverData = null;

        if (section != null) {
            if (!section.hasOnlyAir()) {
                latexCoverData = ReadableContainerExtended.clone(((LevelChunkSectionExtension)section).getLatexStates());
            }
        }

        this.latexCoverStateData = latexCoverData;
    }

    public PalettedContainerRO<LatexCoverState> getLatexCoverData() {
        return this.latexCoverStateData;
    }
}
