package net.ltxprogrammer.changed.advancements.critereon;

import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TransfurTrigger extends SimpleCriterionTrigger<TransfurTrigger.TriggerInstance> {
    static final ResourceLocation ID = Changed.modResource("transfur");

    public ResourceLocation getId() { return ID; }

    public TransfurTrigger.TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext context) {
        return new TransfurTrigger.TriggerInstance(predicate, TransfurPredicate.fromJson(jsonObject));
    }

    public void trigger(ServerPlayer player, TransfurVariantInstance<?> form) {
        this.trigger(player, (predicate) -> predicate.matches(form));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final TransfurPredicate form;

        public TriggerInstance(ContextAwarePredicate entityPredicate, TransfurPredicate transfurPredicate) {
            super(ID, entityPredicate);
            this.form = transfurPredicate;
        }

        public static TriggerInstance transfurred() {
            return new TriggerInstance(ContextAwarePredicate.ANY, TransfurPredicate.ANY);
        }

        public static TriggerInstance transfurred(TransfurPredicate predicate) {
            return new TriggerInstance(ContextAwarePredicate.ANY, predicate);
        }

        public boolean matches(TransfurVariantInstance<?> form) {
            return this.form.matches(form);
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            jsonObject.add("form", this.form.serializeToJson());
            return jsonObject;
        }
    }
}
