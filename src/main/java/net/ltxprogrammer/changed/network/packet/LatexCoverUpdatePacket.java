package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                LatexCoverState.setServerVerifiedAt(level, pos, latexCoverState, 19);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
