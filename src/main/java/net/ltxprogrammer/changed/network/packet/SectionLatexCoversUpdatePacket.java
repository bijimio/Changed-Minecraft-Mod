package net.ltxprogrammer.changed.network.packet;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SectionLatexCoversUpdatePacket implements ChangedPacket {
    private static final int POS_IN_SECTION_BITS = 12;
    private final SectionPos sectionPos;
    private final short[] positions;
    private final LatexCoverState[] states;

    public SectionLatexCoversUpdatePacket(SectionPos sectionPos, ShortSet positions, LevelChunkSection section) {
        this.sectionPos = sectionPos;
        int i = positions.size();
        this.positions = new short[i];
        this.states = new LatexCoverState[i];
        int j = 0;

        for(short short1 : positions) {
            this.positions[j] = short1;
            this.states[j] = LatexCoverState.getAt(section, SectionPos.sectionRelativeX(short1), SectionPos.sectionRelativeY(short1), SectionPos.sectionRelativeZ(short1));
            ++j;
        }

    }

    public SectionLatexCoversUpdatePacket(FriendlyByteBuf buffer) {
        this.sectionPos = SectionPos.of(buffer.readLong());
        int i = buffer.readVarInt();
        this.positions = new short[i];
        this.states = new LatexCoverState[i];

        for(int j = 0; j < i; ++j) {
            long k = buffer.readVarLong();
            this.positions[j] = (short)((int)(k & 4095L));
            this.states[j] = ChangedLatexTypes.getLatexCoverStateIDMap().byId((int)(k >>> 12));
        }

    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(this.sectionPos.asLong());
        buffer.writeVarInt(this.positions.length);

        for(int i = 0; i < this.positions.length; ++i) {
            buffer.writeVarLong((long)ChangedLatexTypes.getLatexCoverStateIDMap().getId(this.states[i]) << 12 | (long)this.positions[i]);
        }

    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for(int i = 0; i < this.positions.length; ++i) {
                    short short1 = this.positions[i];
                    blockpos$mutableblockpos.set(this.sectionPos.relativeToBlockX(short1), this.sectionPos.relativeToBlockY(short1), this.sectionPos.relativeToBlockZ(short1));
                    LatexCoverState.setServerVerifiedAt(level, blockpos$mutableblockpos, this.states[i], 19);
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
