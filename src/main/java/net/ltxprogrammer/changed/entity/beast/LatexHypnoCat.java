package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbilityInstance;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexHypnoCat extends AbstractLatexHypnoCat implements PatronOC {
    protected final SimpleAbilityInstance hypnosis;

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
    }

    public LatexHypnoCat(EntityType<? extends LatexHypnoCat> type, Level level) {
        super(type, level);
        hypnosis = registerAbility(ability -> this.wantToHypno(), new SimpleAbilityInstance(ChangedAbilities.HYPNOSIS.get(), IAbstractChangedEntity.forEntity(this)));
    }

    public boolean wantToHypno() {
        return getTarget() != null;
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.fromInt(0x52596d);
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.SHORT_MESSY.get());
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#333333");
    }
}
