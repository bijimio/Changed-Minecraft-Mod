package net.ltxprogrammer.changed.advancements.critereon;

import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;

public class TameLatexTrigger extends SimpleCriterionTrigger<TameLatexTrigger.TriggerInstance> {
    static final ResourceLocation ID = Changed.modResource("tame_latex");

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject json, ContextAwarePredicate composite, DeserializationContext context) {
        ContextAwarePredicate entitypredicate$composite = EntityPredicate.fromJson(json, "entity", context);
        return new TriggerInstance(composite, entitypredicate$composite);
    }

    public void trigger(ServerPlayer player, ChangedEntity entity) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(lootcontext));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate entity;

        public TriggerInstance(ContextAwarePredicate p_68846_, ContextAwarePredicate p_68847_) {
            super(TameLatexTrigger.ID, p_68846_);
            this.entity = p_68847_;
        }

        public static TriggerInstance tamedAnimal() {
            return new TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance tamedAnimal(EntityPredicate entityPredicate) {
            return new TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(entityPredicate));
        }

        public boolean matches(LootContext p_68853_) {
            return this.entity.matches(p_68853_);
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonobject = super.serializeToJson(context);
            jsonobject.add("entity", this.entity.toJson(context));
            return jsonobject;
        }
    }
}