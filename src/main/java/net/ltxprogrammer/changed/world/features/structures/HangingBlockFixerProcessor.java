package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Intended to locally fix MC-102223
 * Placement processors may opt in to this fix with `HangingBlockFixerProcessor.INSTANCE`
 */
public class HangingBlockFixerProcessor extends StructureProcessor {
    public static final Codec<HangingBlockFixerProcessor> CODEC = Codec.unit(() -> {
        return HangingBlockFixerProcessor.INSTANCE;
    });
    public static final HangingBlockFixerProcessor INSTANCE = new HangingBlockFixerProcessor();

    private HangingBlockFixerProcessor() {
    }

    @Override
    public StructureTemplate.StructureEntityInfo processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo, StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings, StructureTemplate template) {
        entityInfo = super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings, template);

        if (EntityType.by(entityInfo.nbt).orElse(null) == EntityType.PAINTING) {
            // Code adapted from minecraftjibam2 on https://bugs.mojang.com/browse/MC/issues/MC-102223

            var motive = ForgeRegistries.PAINTING_VARIANTS.getValue(ResourceLocation.tryParse(entityInfo.nbt.getString("Motive")));
            var direction = placementSettings.getRotation().rotate(Direction.from2DDataValue(entityInfo.nbt.getByte("Facing")));

            var pos = new BlockPos.MutableBlockPos();
            pos.set(new BlockPos(
                    Mth.floor(entityInfo.pos.x),
                    Mth.floor(entityInfo.pos.y),
                    Mth.floor(entityInfo.pos.z)));

            var width = motive.getWidth() / 16;
            var height = motive.getHeight() / 16;

            // paintings with an even height seem to always be moved upwards...
            if (height % 2 == 0) {
                pos.move(0, -1, 0);
            }

            // paintings with an even width seem to be moved in the clockwise direction of their facing direction,
            // if they're west or south.
            if (width % 2 == 0 && (direction == Direction.WEST || direction == Direction.SOUTH)) {
                var moveTo = direction.getClockWise().getNormal();
                pos.move(moveTo);
            }

            entityInfo = new StructureTemplate.StructureEntityInfo(
                    new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                    entityInfo.blockPos,
                    entityInfo.nbt);
        }

        return entityInfo;
    }

    protected StructureProcessorType<?> getType() {
        return ChangedFeatures.HANGING_BLOCK_FIXER_PROCESSOR.get();
    }
}
