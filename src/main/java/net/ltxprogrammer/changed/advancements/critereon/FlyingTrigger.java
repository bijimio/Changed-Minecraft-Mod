package net.ltxprogrammer.changed.advancements.critereon;

import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class FlyingTrigger extends SimpleCriterionTrigger<FlyingTrigger.TriggerInstance> {
    static final ResourceLocation ID = Changed.modResource("flying");

    public ResourceLocation getId() { return ID; }

    public TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate predicate, DeserializationContext context) {
        return new TriggerInstance(predicate, jsonObject.has("ticks") ? GsonHelper.getAsInt(jsonObject, "ticks") : 0);
    }

    public void trigger(ServerPlayer player, int ticks) {
        this.trigger(player, (predicate) -> predicate.matches(ticks));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final int ticks;

        public TriggerInstance(ContextAwarePredicate entityPredicate, int ticks) {
            super(ID, entityPredicate);
            this.ticks = ticks;
        }

        public boolean matches(int ticks) {
            return this.ticks <= ticks;
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            jsonObject.addProperty("ticks", this.ticks);
            return jsonObject;
        }
    }
}
