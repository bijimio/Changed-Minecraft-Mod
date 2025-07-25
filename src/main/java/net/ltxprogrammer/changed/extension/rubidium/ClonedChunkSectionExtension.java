package net.ltxprogrammer.changed.extension.rubidium;

import me.jellysquid.mods.sodium.client.world.cloned.palette.ClonedPalette;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.util.SimpleBitStorage;

public interface ClonedChunkSectionExtension {
    SimpleBitStorage getLatexCoverData();
    ClonedPalette<LatexCoverState> getLatexCoverPalette();
}
