package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.fluid.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedFluids {
    public static final DeferredRegister<FluidType> REGISTRY_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Changed.MODID);
    public static final DeferredRegister<Fluid> REGISTRY_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Changed.MODID);

    public static final RegistryObject<FluidType> DARK_LATEX_FLUID = REGISTRY_TYPES.register("dark_latex", DarkLatexFluid::createFluidType);
    public static final RegistryObject<FluidType> WHITE_LATEX_FLUID = REGISTRY_TYPES.register("white_latex", WhiteLatexFluid::createFluidType);

    public static final RegistryObject<FluidType> WOLF_TRANSFUR_GAS = REGISTRY_TYPES.register("wolf_transfur_gas", WolfGas::createFluidType);
    public static final RegistryObject<FluidType> TIGER_TRANSFUR_GAS = REGISTRY_TYPES.register("tiger_transfur_gas", TigerGas::createFluidType);
    public static final RegistryObject<FluidType> SKUNK_TRANSFUR_GAS = REGISTRY_TYPES.register("skunk_transfur_gas", SkunkGas::createFluidType);

    public static final RegistryObject<AbstractLatexFluid> DARK_LATEX = REGISTRY_FLUIDS.register("dark_latex", DarkLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> DARK_LATEX_FLOWING = REGISTRY_FLUIDS.register("dark_latex_flowing", DarkLatexFluid.Flowing::new);
    public static final RegistryObject<AbstractLatexFluid> WHITE_LATEX = REGISTRY_FLUIDS.register("white_latex", WhiteLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> WHITE_LATEX_FLOWING = REGISTRY_FLUIDS.register("white_latex_flowing", WhiteLatexFluid.Flowing::new);

    public static final RegistryObject<TransfurGas> WOLF_GAS = register("wolf_gas", WolfGas.Source::new, ChangedFluids::translucentRenderer);
    public static final RegistryObject<TransfurGas> WOLF_GAS_FLOWING = register("wolf_gas_flowing", WolfGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static final RegistryObject<TransfurGas> TIGER_GAS = register("tiger_gas", TigerGas.Source::new, ChangedFluids::translucentRenderer);
    public static final RegistryObject<TransfurGas> TIGER_GAS_FLOWING = register("tiger_gas_flowing", TigerGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static final RegistryObject<TransfurGas> SKUNK_GAS = register("skunk_gas", SkunkGas.Source::new, ChangedFluids::translucentRenderer);
    public static final RegistryObject<TransfurGas> SKUNK_GAS_FLOWING = register("skunk_gas_flowing", SkunkGas.Flowing::new, ChangedFluids::translucentRenderer);

    public static void cutoutRenderer(Fluid fluid) {
        /*DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(fluid, renderType -> renderType == RenderType.cutout()));*/
    }

    public static void translucentRenderer(Fluid fluid) {
        /*DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(fluid, renderType -> renderType == RenderType.translucent()));*/
    }

    public static <T extends Fluid, F extends T> RegistryObject<T> register(String name, Supplier<F> fluid) {
        return register(name, fluid, null);
    }

    public static <T extends Fluid, F extends T> RegistryObject<T> register(String name, Supplier<F> fluid, @Nullable Consumer<Fluid> renderLayer) {
        return REGISTRY_FLUIDS.register(name, fluid);
    }
}
