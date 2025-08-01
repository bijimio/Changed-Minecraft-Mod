package net.ltxprogrammer.changed.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.Set;

public class TransfurPredicate {
    public static final TransfurPredicate ANY = new TransfurPredicate();
    @Nullable
    private final Set<TransfurVariant<?>> forms;
    @Nullable
    private final LatexType type;
    private final boolean flying;
    private final boolean swimming;
    private final boolean legless;

    public TransfurPredicate() {
        this.forms = null;
        this.type = null;
        this.flying = false;
        this.swimming = false;
        this.legless = false;
    }

    public TransfurPredicate(LatexType type) {
        this.forms = null;
        this.type = type;
        this.flying = false;
        this.swimming = false;
        this.legless = false;
    }

    public TransfurPredicate(Set<TransfurVariant<?>> forms) {
        this.forms = forms;
        this.type = null;
        this.flying = false;
        this.swimming = false;
        this.legless = false;
    }

    public TransfurPredicate(boolean flying, boolean swimming, boolean legless) {
        this.forms = null;
        this.type = null;
        this.flying = flying;
        this.swimming = swimming;
        this.legless = legless;
    }

    public boolean matches(TransfurVariantInstance<?> form) {
        if (this == ANY)
            return true;
        if (forms != null)
            for (TransfurVariant<?> setForm : forms)
                if (setForm.getFormId() == form.getFormId())
                    return true;
        if (type != null)
            return form.getLatexType() == type;
        if (form.getParent().canGlide && flying)
            return true;
        if (form.getParent().getBreatheMode().canBreatheWater() && swimming)
            return true;
        if (form.getEntityShape().isLegless() && legless)
            return true;
        return false;
    }

    public static TransfurPredicate fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "form");
            if (jsonObject.has("type")) {
                final LatexType type = LatexType.valueOf(GsonHelper.getAsString(jsonObject, "type"));
                return new TransfurPredicate(type);
            }
            if (jsonObject.has("forms")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "forms");
                if (jsonArray != null) {
                    ImmutableSet.Builder<TransfurVariant<?>> builder = ImmutableSet.builder();
                    for (var element : jsonArray) {
                        ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.convertToString(element, "form"));
                        if (!ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(resourcelocation))
                            throw new JsonSyntaxException("Unknown form id '" + resourcelocation + "'");
                        builder.add(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(resourcelocation));
                    }

                    Set<TransfurVariant<?>> set = builder.build();
                    if (!set.isEmpty())
                        return new TransfurPredicate(set);
                }
            }
            boolean flying = false, swimming = false, legless = false;
            if (jsonObject.has("flying"))
                flying = GsonHelper.getAsBoolean(jsonObject, "flying");
            if (jsonObject.has("swimming"))
                swimming = GsonHelper.getAsBoolean(jsonObject, "swimming");
            if (jsonObject.has("legless"))
                legless = GsonHelper.getAsBoolean(jsonObject, "legless");
            return (flying || swimming || legless) ? new TransfurPredicate(flying, swimming, legless) : ANY;
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY)
            return JsonNull.INSTANCE;
        else {
            JsonObject jsonObject = new JsonObject();
            if (this.forms != null) {
                JsonArray jsonArray = new JsonArray();

                for (var form : this.forms) {
                    jsonArray.add(form.getFormId().toString());
                }

                jsonObject.add("forms", jsonArray);
            }

            if (this.type != null) {
                jsonObject.addProperty("type", this.type.toString());
            }

            jsonObject.addProperty("flying", this.flying);
            jsonObject.addProperty("swimming", this.swimming);
            jsonObject.addProperty("legless", this.legless);
            return jsonObject;
        }
    }
}
