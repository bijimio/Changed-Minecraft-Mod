package net.ltxprogrammer.changed.mixin.compatibility.Rubidium;

import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import me.jellysquid.mods.sodium.client.world.cloned.PackedIntegerArrayExtended;
import me.jellysquid.mods.sodium.client.world.cloned.palette.ClonedPalette;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.rubidium.ClonedChunkSectionExtension;
import net.ltxprogrammer.changed.extension.rubidium.WorldSliceExtension;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = WorldSlice.class, remap = false)
@RequiredMods("rubidium")
public abstract class WorldSliceMixin implements WorldSliceExtension {
    @Shadow @Final private static int SECTION_TABLE_ARRAY_SIZE;

    @Shadow private int baseX;
    @Shadow private int baseY;
    @Shadow private int baseZ;

    @Shadow public abstract BlockState getBlockState(int x, int y, int z);

    @Shadow private SectionPos origin;
    @Shadow @Final private static int SECTION_LENGTH;
    @Shadow private ClonedChunkSection[] sections;
    @Unique private final LatexCoverState[][] latexCoverStatesArrays = new LatexCoverState[SECTION_TABLE_ARRAY_SIZE][4096];

    @Unique
    private void unpackLatexCoverData(LatexCoverState[] states, ClonedChunkSection section, BoundingBox box) {
        if (this.origin.equals(section.getPosition())) {
            this.unpackLatexCoverData(states, section);
        } else {
            this.unpackLatexCoverDataSlow(states, section, box);
        }
    }

    @Unique
    private void unpackLatexCoverData(LatexCoverState[] states, ClonedChunkSection section) {
        ((PackedIntegerArrayExtended)((ClonedChunkSectionExtension)section).getLatexCoverData())
                .copyUsingPalette(states, ((ClonedChunkSectionExtension) section).getLatexCoverPalette());
    }

    @Unique
    private void unpackLatexCoverDataSlow(LatexCoverState[] states, ClonedChunkSection section, BoundingBox box) {
        SimpleBitStorage intArray = ((ClonedChunkSectionExtension) section).getLatexCoverData();
        ClonedPalette<LatexCoverState> palette = ((ClonedChunkSectionExtension) section).getLatexCoverPalette();
        SectionPos pos = section.getPosition();
        int minBlockX = Math.max(box.minX(), pos.minBlockX());
        int maxBlockX = Math.min(box.maxX(), pos.maxBlockX());
        int minBlockY = Math.max(box.minY(), pos.minBlockY());
        int maxBlockY = Math.min(box.maxY(), pos.maxBlockY());
        int minBlockZ = Math.max(box.minZ(), pos.minBlockZ());
        int maxBlockZ = Math.min(box.maxZ(), pos.maxBlockZ());

        for(int y = minBlockY; y <= maxBlockY; ++y) {
            for(int z = minBlockZ; z <= maxBlockZ; ++z) {
                for(int x = minBlockX; x <= maxBlockX; ++x) {
                    int blockIdx = WorldSlice.getLocalBlockIndex(x & 15, y & 15, z & 15);
                    int value = intArray.get(blockIdx);
                    states[blockIdx] = (LatexCoverState) palette.get(value);
                }
            }
        }

    }

    @Override
    public LatexCoverState getLatexCoverState(int x, int y, int z) {
        final BlockState blockState = getBlockState(x, y, z);
        if (blockState.getBlock() instanceof LatexCoveringSource source)
            return source.getLatexCoverState(blockState, new BlockPos(x, y, z));

        int relX = x - this.baseX;
        int relY = y - this.baseY;
        int relZ = z - this.baseZ;
        LatexCoverState var10000 = this.latexCoverStatesArrays[WorldSlice.getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4)][WorldSlice.getLocalBlockIndex(relX & 15, relY & 15, relZ & 15)];
        return Objects.requireNonNullElseGet(var10000, () -> ChangedLatexTypes.NONE.get().defaultCoverState());
    }

    @Inject(method = "copyData", at = @At("TAIL"))
    public void copyChangedData(ChunkRenderContext context, CallbackInfo ci) {
        for(int x = 0; x < SECTION_LENGTH; ++x) {
            for(int y = 0; y < SECTION_LENGTH; ++y) {
                for(int z = 0; z < SECTION_LENGTH; ++z) {
                    int idx = WorldSlice.getLocalSectionIndex(x, y, z);
                    this.unpackLatexCoverData(this.latexCoverStatesArrays[idx], this.sections[idx], context.getVolume());
                }
            }
        }
    }
}
