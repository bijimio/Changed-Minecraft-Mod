package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.network.packet.TransfurEntityEventPacket;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @Inject(method = "broadcastEntityEvent", at = @At("HEAD"), cancellable = true)
    public void maybeBroadcastForVariant(Entity entity, byte id, CallbackInfo ci) {
        if (entity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null) {
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(changedEntity::getUnderlyingPlayer),
                    new TransfurEntityEventPacket(changedEntity.getUnderlyingPlayer(), id));
            ci.cancel();
        }
    }

    @Inject(method = "tickChunk", at = @At("TAIL"))
    public void doChangedTicks(LevelChunk chunk, int tickCount, CallbackInfo ci,
                               @Local ProfilerFiller profilerFiller) {
        profilerFiller.push("changed:latexCoverTick");

        ChunkPos chunkpos = chunk.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();

        if (tickCount > 0) {
            LevelChunkSection[] alevelchunksection = chunk.getSections();

            for(int l = 0; l < alevelchunksection.length; ++l) {
                LevelChunkSection levelchunksection = alevelchunksection[l];
                if (levelchunksection.isRandomlyTicking()) {
                    int j1 = chunk.getSectionYFromSectionIndex(l);
                    int k1 = SectionPos.sectionToBlockCoord(j1);

                    for(int l1 = 0; l1 < tickCount; ++l1) {
                        BlockPos blockPos = this.getBlockRandomPos(i, k1, j, 15);
                        profilerFiller.push("randomTick");
                        LatexCoverState coverState = LatexCoverState.getAt(levelchunksection, blockPos.getX() - i, blockPos.getY() - k1, blockPos.getZ() - j);

                        if (coverState.isRandomlyTicking())
                            coverState.randomTick((ServerLevel)(Object)this, blockPos, this.random);

                        profilerFiller.pop();
                    }
                }
            }
        }

        profilerFiller.pop();
    }
}
