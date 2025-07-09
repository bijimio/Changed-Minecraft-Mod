package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public abstract class AbstractLatexFluid extends ForgeFlowingFluid {
    private final List<Supplier<? extends TransfurVariant<?>>> form;
    private final LatexType gooType;

    protected AbstractLatexFluid(Properties properties, LatexType gooType, List<Supplier<? extends TransfurVariant<?>>> form) {
        super(properties);
        this.gooType = gooType;
        this.form = form;
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    public LatexType getLatexType() {
        return gooType;
    }

    public abstract boolean canEntityStandOn(LivingEntity entity);

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Level level = event.getEntity().level();
        AbstractLatexFluid fluid = null;
        BlockState state = Blocks.AIR.defaultBlockState();
        if (level.getFluidState(event.getEntity().blockPosition()).getType() instanceof AbstractLatexFluid fluidFeet) {
            state = level.getBlockState(event.getEntity().blockPosition());
            fluid = fluidFeet;
        }
        if (level.getFluidState(EntityUtil.getEyeBlock(event.getEntity())).getType() instanceof AbstractLatexFluid fluidHead) {
            state = level.getBlockState(event.getEntity().blockPosition());
            fluid = fluidHead;
        }

        if (fluid != null) {
            if (TransfurVariant.getEntityVariant(event.getEntity()) != null) {
                var living = event.getEntity();
                var delta = living.getDeltaMovement();
                living.resetFallDistance();
                living.setDeltaMovement(living.getDeltaMovement().multiply(1.0, delta.y > 0.0 ? 1.1 : 0.5, 1.0));
            } else
                event.getEntity().makeStuckInBlock(state, new Vec3(0.75, 0.75, 0.75));
        }

        if (event.getEntity().isAlive() && !event.getEntity().isDeadOrDying() && fluid != null) {
            TransfurVariant<?> variant = TransfurVariant.getEntityVariant(event.getEntity());
            if (variant == null)
                ProcessTransfur.progressTransfur(event.getEntity(), 5.0f, fluid.form.get(level.random.nextInt(fluid.form.size())).get(),
                        TransfurContext.hazard(TransfurCause.LATEX_PUDDLE));
            else if (variant.getLatexType().isHostileTo(fluid.gooType))
                event.getEntity().hurt(ChangedDamageSources.LATEX_FLUID.source(event.getEntity().level().registryAccess()), 2.0f);
        }
    }

    private void fizz(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
    }

    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction != Direction.UP) {
            FluidState otherState = level.getFluidState(pos);
            if (this.is(ChangedTags.Fluids.LATEX) && otherState.is(FluidTags.LAVA)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, pos,
                            Blocks.SOUL_SOIL.defaultBlockState().setValue(AbstractLatexBlock.COVERED, this.gooType)), 3);
                }

                this.fizz(level, pos);
                return;
            }
        }

        super.spreadTo(level, pos, blockState, direction, fluidState);
    }
}
