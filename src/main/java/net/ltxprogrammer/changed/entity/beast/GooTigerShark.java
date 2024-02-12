package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbilityInstance;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GooTigerShark extends AbstractAquaticEntity {
    protected final SimpleAbilityInstance summonSharks;

    public GooTigerShark(EntityType<? extends GooTigerShark> type, Level level) {
        super(type, level);
        summonSharks = registerAbility(ability -> this.wantToSummon(), new SimpleAbilityInstance(ChangedAbilities.SUMMON_SHARKS.get(), IAbstractChangedEntity.forChangedEntity(this)));
    }

    public boolean wantToSummon() {
        return getTarget() != null;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.SHORT_MESSY.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#969696");
    }
}
