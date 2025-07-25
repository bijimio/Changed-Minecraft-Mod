package net.ltxprogrammer.changed.mixin;

import com.mojang.datafixers.DataFixer;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataFixTypes.class)
public class DataFixTypesMixin {
    @Inject(method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    public void updateChanged(DataFixer fixer, CompoundTag tag, int taggedVersion, int loadedVersion, CallbackInfoReturnable<CompoundTag> cir) {
        if (Changed.dataFixer != null)
            Changed.dataFixer.updateCompoundTag((DataFixTypes)(Object)this, tag);
    }
}
