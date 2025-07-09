package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

public class LatexSwimMover extends PlayerMover<LatexSwimMover.MoverInstance> {
    @Override
    public LatexSwimMover.MoverInstance createInstance() {
        return new LatexSwimMover.MoverInstance(this);
    }

    public static class MoverInstance extends PlayerMoverInstance<LatexSwimMover> {
        private Vec3 lastPos = null;
        private int ticksInLatex = 0;
        private static final double ACCELERATION = 0.2;
        private static final double DECAY = 0.65;
        private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);

        public MoverInstance(LatexSwimMover parent) {
            super(parent);
        }

        public TransfurVariantInstance<?> getForm(Player player) {
            return ProcessTransfur.getPlayerTransfurVariant(player);
        }

        public LatexType getExpectedLatexType(LivingEntity entity) {
            return ChangedLatexTypes.WHITE_LATEX.get();
        }

        @Override
        public void saveTo(CompoundTag tag) {
            super.saveTo(tag);
            tag.putInt("ticksInLatex", ticksInLatex);
        }

        @Override
        public void readFrom(CompoundTag tag) {
            super.readFrom(tag);
            ticksInLatex = tag.getInt("ticksInLatex");
        }

        @Override
        public void aiStep(Player player, InputWrapper input, LogicalSide side) {
            if (lastPos == null)
                lastPos = player.getPosition(1.0f);

            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                if (variant.getLatexType().isHostileTo(net.ltxprogrammer.changed.entity.LatexType.WHITE_LATEX))
                    player.hurt(ChangedDamageSources.WHITE_LATEX.source(player.level().registryAccess()), 2.0f);
            }, () -> {
                ProcessTransfur.progressTransfur(player, 4.8f, ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX));
            });

            player.setDeltaMovement(0, 0, 0);
            player.refreshDimensions();
            player.heal(0.0625F);
            if (player.tickCount % 50 == 0)
                player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
            player.resetFallDistance();

            Vec3 currentPos = player.getPosition(1.0f);
            Vec3 velocity = currentPos.subtract(lastPos).multiply(DECAY, DECAY, DECAY);
            if (velocity.distanceToSqr(Vec3.ZERO) < 0.00000625)
                velocity = Vec3.ZERO;

            Vec3 lookAngle = player.getLookAngle();
            Vec3 upAngle = player.getUpVector(1.0f);
            Vec3 leftAngle = upAngle.cross(lookAngle);

            Vec2 horizontal = input.getMoveVector();
            double vertical = (input.getJumping() ? 1.0 : 0.0) + (input.getShiftKeyDown() ? -1.0 : 0.0);

            double moveSpeed = (input.getSprintKeyDown() ? 1.5 : 0.85) * ACCELERATION;

            Vec3 controlDir = lookAngle.multiply(horizontal.y, horizontal.y, horizontal.y)
                    .add(UP.multiply(vertical, vertical, vertical))
                    .add(leftAngle.multiply(horizontal.x, horizontal.x, horizontal.x)).normalize().multiply(moveSpeed, moveSpeed, moveSpeed);

            player.move(MoverType.SELF, controlDir.add(velocity));
            lastPos = currentPos;

            ticksInLatex++;
        }

        @Override
        public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {
            var form = getForm(player);
            if (player instanceof ServerPlayer serverPlayer && form != null)
                ChangedCriteriaTriggers.WHITE_LATEX_FUSE.trigger(serverPlayer, ticksInLatex);
        }

        public boolean isInsideSwimableMaterial(LivingEntity entity) {
            AABB testHitbox = entity.getBoundingBox().inflate(-0.05);
            final LatexType expectedLatexType = getExpectedLatexType(entity);
            return BlockPos.betweenClosedStream(testHitbox).anyMatch(blockPos -> {
                final BlockState blockState = entity.level().getBlockState(blockPos);
                final LatexCoverState coverState = LatexCoverState.getAt(entity.level(), blockPos);
                if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                    return transportInterface.allowTransport(blockState);
                if (!coverState.isAir() && coverState.getType() == expectedLatexType) {
                    var shape = coverState.getCollisionShape(LatexCoverGetter.wrap(entity.level()), blockPos, CollisionContext.empty()).move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
                    return Shapes.joinIsNotEmpty(shape, Shapes.create(testHitbox), BooleanOp.AND);
                }

                return false;
            });
        }

        @Override
        public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
            return !isInsideSwimableMaterial(player) || player.isSpectator() ||
                    ProcessTransfur.getPlayerTransfurVariantSafe(player)
                            .map(variant -> variant.getLatexType() != net.ltxprogrammer.changed.entity.LatexType.WHITE_LATEX).orElse(false);
        }

        @Override
        public EntityDimensions getDimensions(Pose pose, EntityDimensions currentDimensions) {
            return EntityDimensions.fixed(2.0f / 16.0f, 2.0f / 16.0f);
        }

        @Override
        public void onRemove(Player player) {
            super.onRemove(player);

            if (player.isSpectator())
                return;

            player.setInvulnerable(false);
            player.playSound(ChangedSounds.POISON.get(), 1.0f, 1.0f);
        }
    }
}