package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedDamageSources {
    public static final DeferredRegister<DamageType> REGISTRY = DeferredRegister.create(Registries.DAMAGE_TYPE, Changed.MODID);

    public record DamageTypeHolder(RegistryObject<DamageType> registryObject) implements Supplier<DamageType> {
        public DamageSource source() {
            return new DamageSource(registryObject.getHolder().orElseThrow());
        }

        public DamageSource source(Entity sourceEntity) {
            return new DamageSource(registryObject.getHolder().orElseThrow(), sourceEntity);
        }

        @Override
        public DamageType get() {
            return registryObject.get();
        }
    }

    protected static DamageTypeHolder register(String name, Function<String, DamageType> value) {
        return new DamageTypeHolder(REGISTRY.register(name, () -> value.apply(Changed.modResourceStr(name))));
    }

    public static final DamageTypeHolder TRANSFUR = register("transfur", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder ABSORB = register("absorb", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder BLOODLOSS = register("bloodloss", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder ELECTROCUTION = register("electrocution", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder WHITE_LATEX = register("white_latex", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder LATEX_FLUID = register("latex_fluid", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder PALE = register("pale", id -> new DamageType(id, 0.1f));
    public static final DamageTypeHolder FAN = register("fan", id -> new DamageType(id, 0.1f));

    public static DamageSource entityTransfur(LivingEntity source) {
        return TRANSFUR.source(source);
    }

    public static DamageSource entityTransfur(@Nullable IAbstractChangedEntity source) {
        return TRANSFUR.source(source == null ? null : source.getEntity());
    }

    public static DamageSource entityAbsorb(LivingEntity source) {
        return TRANSFUR.source(source);
    }

    public static DamageSource entityAbsorb(@Nullable IAbstractChangedEntity source) {
        return TRANSFUR.source(source == null ? null : source.getEntity());
    }
}
