package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.packet.LatexCoverUpdatePacket;
import net.ltxprogrammer.changed.network.packet.SectionLatexCoversUpdatePacket;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {
    @Shadow protected abstract void broadcast(List<ServerPlayer> p_288998_, Packet<?> p_289013_);

    @Inject(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;broadcastBlockEntityIfNeeded(Ljava/util/List;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void broadcastChangedChangesSingle(LevelChunk chunk, CallbackInfo ci,
                                        @Local List<ServerPlayer> listeners,
                                        @Local BlockPos blockPos) {
        this.broadcast(listeners, Changed.PACKET_HANDLER.toVanillaPacket(
                new LatexCoverUpdatePacket(blockPos, LatexCoverState.getAt(chunk, blockPos)), NetworkDirection.PLAY_TO_CLIENT));
    }

    @Inject(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSectionBlocksUpdatePacket;runUpdates(Ljava/util/function/BiConsumer;)V"))
    public void broadcastChangedChangesMulti(LevelChunk chunk, CallbackInfo ci,
                                             @Local List<ServerPlayer> listeners,
                                             @Local LevelChunkSection section,
                                             @Local SectionPos sectionPos,
                                             @Local ShortSet blockPositions) {
        this.broadcast(listeners, Changed.PACKET_HANDLER.toVanillaPacket(
                new SectionLatexCoversUpdatePacket(sectionPos, blockPositions, section), NetworkDirection.PLAY_TO_CLIENT));
    }
}
