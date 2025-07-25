package net.ltxprogrammer.changed.mixin.network;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.LatexCoverHitResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {
    @Shadow public abstract <T> void writeOptional(Optional<T> p_236836_, FriendlyByteBuf.Writer<T> p_236837_);

    @Shadow public abstract <T> Optional<T> readOptional(FriendlyByteBuf.Reader<T> p_236861_);

    @Unique
    private static final ResourceLocation EXTENDED_HIT_RESULT = Changed.modResource("latex_cover_hit_result");

    // These should sequence in the same order
    @WrapMethod(method = "writeBlockHitResult")
    public void writeExtendedHitResult(BlockHitResult hitResult, Operation<Void> original) {
        original.call(hitResult);
        this.writeOptional(hitResult instanceof LatexCoverHitResult coverHitResult ? Optional.of(coverHitResult) : Optional.empty(), (buffer, coverHitResult) -> {
            buffer.writeResourceLocation(EXTENDED_HIT_RESULT);
        });
    }

    @WrapMethod(method = "readBlockHitResult")
    public BlockHitResult readExtendedHitResult(Operation<BlockHitResult> original) {
        final BlockHitResult hitResult = original.call();
        return this.readOptional((buffer) -> {
            return buffer.readResourceLocation().equals(EXTENDED_HIT_RESULT) ? LatexCoverHitResult.wrap(hitResult) : hitResult;
        }).orElse(hitResult);
    }
}
