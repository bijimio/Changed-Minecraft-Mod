package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LatexCoverUpdatePacket implements ChangedPacket {
    private final BlockPos pos;
    private final LatexCoverState latexCoverState;

    public LatexCoverUpdatePacket(BlockPos pos, LatexCoverState state) {
        this.pos = pos;
        this.latexCoverState = state;
    }

    public LatexCoverUpdatePacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.latexCoverState = buffer.readById(ChangedLatexTypes.getLatexCoverStateIDMap());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeId(ChangedLatexTypes.getLatexCoverStateIDMap(), this.latexCoverState);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            LatexCoverState.setServerVerifiedAt(UniversalDist.getLevel(), pos, latexCoverState, 19);
        }
    }
}
