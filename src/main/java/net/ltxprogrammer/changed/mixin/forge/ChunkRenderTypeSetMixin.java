package net.ltxprogrammer.changed.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.Iterator;
import java.util.function.Function;

@Mixin(value = ChunkRenderTypeSet.class, remap = false)
public abstract class ChunkRenderTypeSetMixin implements Iterator<RenderType> {
    @Unique
    private Function<RenderType, RenderType> renderTypeOverride = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onCreate(BitSet bits, CallbackInfo ci) {
        this.renderTypeOverride = ChangedClient.acceptNextRenderTypeSetOverride();
    }

    @WrapMethod(method = "iterator")
    public Iterator<RenderType> overriddenIterator(Operation<Iterator<RenderType>> original) {
        if (renderTypeOverride == null)
            return original.call();
        else
            return CollectionUtil.mapIterator(original.call(), renderTypeOverride);
    }
}
