package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Consumer;

public abstract class TigerGas extends TransfurGas {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ChangedFluids.TIGER_TRANSFUR_GAS, ChangedFluids.TIGER_GAS, ChangedFluids.TIGER_GAS_FLOWING)
            .tickRate(4)
            .levelDecreasePerBlock(1)
            .explosionResistance(100f)
            .block(ChangedBlocks.TIGER_GAS);

    public static FluidType createFluidType() {
        return new GasFluidType(Gas.createProperties().descriptionId("tiger_transfur_gas")) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    private static final ResourceLocation GAS_STILL = Changed.modResource("block/tiger_gas");
                    private static final ResourceLocation GAS_FLOW = Changed.modResource("block/tiger_gas");

                    public ResourceLocation getStillTexture() {
                        return GAS_STILL;
                    }

                    public ResourceLocation getFlowingTexture() {
                        return GAS_FLOW;
                    }

                    public int getTintColor() {
                        return 0x7FFFFFFF;
                    }
                });
            }
        };
    }

    protected TigerGas() {
        super(PROPERTIES, ChangedTransfurVariants.GAS_TIGER);
    }

    @Override
    public Color3 getColor() {
        return Color3.fromInt(0xffba44);
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return ChangedBlocks.TIGER_GAS.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
    }

    public static class Flowing extends TigerGas {
        public Flowing() {
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends TigerGas {
        public Source() {}

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
