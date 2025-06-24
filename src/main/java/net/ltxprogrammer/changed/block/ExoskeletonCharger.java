package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.robot.AbstractRobot;
import net.ltxprogrammer.changed.entity.robot.ChargerType;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.stream.Stream;

public class ExoskeletonCharger extends AbstractLargePanel implements IRobotCharger {
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public ExoskeletonCharger() {
        super(Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 9.0F)
                .randomTicks());
        this.registerDefaultState(this.defaultBlockState()
                .setValue(OCCUPIED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OCCUPIED);
    }

    @Override
    public ChargerType getChargerType() {
        return ChargerType.EXOSKELETON;
    }

    @Override
    public void acceptRobot(BlockState state, Level level, BlockPos pos, AbstractRobot robot) {
        if (state.getValue(OCCUPIED))
            return;

        robot.setCharging(true);
        robot.setSleepingPos(pos);

        final var thisSection = state.getValue(SECTION);
        final var thisFacing = state.getValue(FACING);
        for (var section : NineSection.values()) {
            final var otherPos = thisSection.getRelative(pos, thisFacing, section);
            final var otherState = level.getBlockState(otherPos);
            if (otherState.is(this))
                level.setBlockAndUpdate(otherPos, otherState.setValue(OCCUPIED, true));
        }
    }

    @Override
    public void acceptRobotRemoved(BlockState state, Level level, BlockPos pos, @Nullable AbstractRobot oldRobot) {
        if (!state.getValue(OCCUPIED))
            return;

        if (oldRobot != null && oldRobot.isCharging()) {
            oldRobot.setCharging(false);
            oldRobot.clearSleepingPos();
        }

        final var thisSection = state.getValue(SECTION);
        final var thisFacing = state.getValue(FACING);
        for (var section : NineSection.values()) {
            final var otherPos = thisSection.getRelative(pos, thisFacing, section);
            final var otherState = level.getBlockState(otherPos);
            if (otherState.is(this))
                level.setBlockAndUpdate(otherPos, otherState.setValue(OCCUPIED, false));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (state.getValue(SECTION) == NineSection.CENTER)
            broadcastPosition(level, pos, !state.getValue(OCCUPIED));
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        if (player instanceof Exoskeleton)
            return true;
        return super.isBed(state, level, pos, player);
    }

    @Override
    public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACING).getOpposite();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHit) {
        if (state.getValue(OCCUPIED))
            return InteractionResult.PASS;

        final var exoskeletonItem = AccessorySlots.getForEntity(player).map(AccessorySlots::getItems).orElse(Stream.empty())
                .filter(item -> item.is(ChangedItems.EXOSKELETON.get()))
                .findFirst();

        return exoskeletonItem.map(itemStack -> itemStack.useOn(new UseOnContext(level, player, hand, itemStack, blockHit)))
                .orElseGet(() -> super.use(state, level, pos, player, hand, blockHit));
    }
}
