package net.ltxprogrammer.changed.mixin;

import com.mojang.datafixers.DataFixer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.ChangedDataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(NbtUtils.class)
public abstract class NbtUtilsMixin {
    @Redirect(method = "setValueHelper", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/properties/Property;getValue(Ljava/lang/String;)Ljava/util/Optional;"))
    private static <T extends Comparable<T>> Optional<T> getValueAndUpdate(Property<T> instance, String s) {
        if (Changed.dataFixer != null)
            return instance.getValue(s).or(() -> Changed.dataFixer.updateBlockState(instance, s));
        else
            return instance.getValue(s);
    }
}
