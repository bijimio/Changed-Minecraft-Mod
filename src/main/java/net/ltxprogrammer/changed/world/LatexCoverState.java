package net.ltxprogrammer.changed.world;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class LatexCoverState extends StateHolder<Level, LatexCoverState> {
    public static final EnumProperty<LatexType> COVERED = EnumProperty.create("covered_with", LatexType.class, LatexType.values());
    public static final IntegerProperty SATURATION = IntegerProperty.create("saturation", 0, 8);
    public static final StateDefinition<Level, LatexCoverState> DEFINITION = Util.make(new StateDefinition.Builder<Level, LatexCoverState>(null), builder -> {
        builder.add(COVERED, SATURATION);
    }).create(LatexCoverState::levelDefaultState, LatexCoverState::new);
    public static final Codec<LatexCoverState> CODEC = codec(Codec.unit(() -> null), LatexCoverState::levelDefaultState).stable();

    public static final IdMap<LatexCoverState> PERMUTATIONS = new IdMap<>() {
        private final List<LatexCoverState> all = DEFINITION.getPossibleStates();

        @Override
        public int getId(LatexCoverState state) {
            return all.indexOf(state);
        }

        @Override
        public @Nullable LatexCoverState byId(int p_122651_) {
            return all.get(p_122651_);
        }

        @Override
        public int size() {
            return all.size();
        }

        @Override
        public @NotNull Iterator<LatexCoverState> iterator() {
            return all.iterator();
        }
    };

    public static LatexCoverState defaultState() {
        return DEFINITION.any().setValue(COVERED, LatexType.NEUTRAL).setValue(SATURATION, 0);
    }

    private static LatexCoverState levelDefaultState(Level level) {
        return DEFINITION.any().setValue(COVERED, LatexType.NEUTRAL).setValue(SATURATION, 0);
    }

    protected LatexCoverState(Level level, ImmutableMap<Property<?>, Comparable<?>> properties, MapCodec<LatexCoverState> codec) {
        super(level, properties, codec);
    }

    public static LatexCoverState getAt(Level level, BlockPos blockPos) {
        return ((LevelChunkSectionExtension)level.getChunk(blockPos)
                .getSection(level.getSectionIndex(blockPos.getY())))
                .getLatexCoverState(blockPos.getX(), blockPos.getY(), blockPos.getY());
    }
}
