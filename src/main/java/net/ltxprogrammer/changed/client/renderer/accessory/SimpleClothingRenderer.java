package net.ltxprogrammer.changed.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorHumanModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Clothing;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleClothingRenderer implements AccessoryRenderer, TransitionalAccessory {
    public static record ModelComponent(ArmorModel armorModel, EquipmentSlot renderAs) {}

    protected final HumanoidModel clothingModel;
    protected final Set<ModelComponent> components;

    public SimpleClothingRenderer(ArmorModel humanoid, Set<ModelComponent> components) {
        this.components = components;
        clothingModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ArmorHumanModel.MODEL_SET.getModelName(humanoid)));
    }

    @Override
    public Optional<HumanoidModel<?>> getBeforeModel(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent) {
        return Optional.of(clothingModel);
    }

    @Override
    public Stream<AdvancedHumanoidModel<?>> getAfterModels(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent) {
        if (renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer && EntityUtil.maybeGetOverlaying(slotContext.wearer()) instanceof ChangedEntity wearer) {
            final LatexHumanoidArmorLayer layer = advancedHumanoidRenderer.getArmorLayer();
            return components.stream().map(component -> Optional.of((LatexHumanoidArmorModel<?,?>) layer.modelPicker.getModelSetForSlot(wearer, component.renderAs)
                    .get(component.armorModel))).filter(Optional::isPresent).map(Optional::get);
        }

        return Stream.empty();
    }

    @Override
    public Optional<ResourceLocation> getModelTexture(AccessorySlotContext<?> slotContext) {
        if (slotContext.stack().getItem() instanceof Clothing clothing)
            return Optional.ofNullable(clothing.getTexture(slotContext.stack(), slotContext.wearer()));
        else
            return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(AccessorySlotContext<T> slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = slotContext.stack();
        if (stack.getItem() instanceof Clothing clothing) {
            final T entity = slotContext.wearer();
            ResourceLocation texture = clothing.getTexture(stack, entity);
            if (texture == null) return;

            if (entity instanceof ChangedEntity changedEntity && renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
                final var layer = advancedHumanoidRenderer.getArmorLayer();
                for (var component : components) {
                    final LatexHumanoidArmorModel model = (LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(changedEntity, component.renderAs)
                            .get(component.armorModel);

                    if (advancedHumanoidRenderer.getModel(changedEntity) instanceof AdvancedHumanoidModelInterface advancedModel)
                        model.getAnimator(changedEntity).copyProperties(advancedModel.getAnimator(changedEntity));
                    model.prepareMobModel(changedEntity, limbSwing, limbSwingAmount, partialTicks);
                    model.setupAnim(changedEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                    model.prepareVisibility(component.renderAs, stack);
                    model.renderForSlot(changedEntity, advancedHumanoidRenderer, stack, component.renderAs, matrixStack,
                            ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                            light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    model.unprepareVisibility(component.renderAs, stack);
                }
            } else if (renderLayerParent.getModel() instanceof HumanoidModel<?> baseModel) {
                baseModel.copyPropertiesTo(clothingModel);
                clothingModel.renderToBuffer(matrixStack,
                        ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void renderFirstPersonOnArms(AccessorySlotContext<T> slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, HumanoidArm arm, PoseStack stackCorrector, float partialTicks) {
        ItemStack stack = slotContext.stack();
        if (stack.getItem() instanceof Clothing clothing) {
            final T entity = slotContext.wearer();
            ResourceLocation texture = clothing.getTexture(stack, entity);
            if (texture == null) return;

            if (entity instanceof ChangedEntity changedEntity && renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
                final var layer = advancedHumanoidRenderer.getArmorLayer();
                for (var component : components) {
                    if (component.renderAs != EquipmentSlot.CHEST) continue;

                    final LatexHumanoidArmorModel model = (LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(changedEntity, component.renderAs)
                            .get(component.armorModel);

                    model.prepareMobModel(changedEntity, 0f, 0f, partialTicks);
                    model.setupAnim(changedEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                    model.setupHand(changedEntity);
                    model.prepareVisibility(component.renderAs, stack);
                    FormRenderHandler.renderModelPartWithTexture(model.getArm(arm),
                            stackCorrector, matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                            light, 1F);
                    model.unprepareVisibility(component.renderAs, stack);
                }
            } else if (renderLayerParent.getModel() instanceof HumanoidModel<?> baseModel) {
                baseModel.copyPropertiesTo(clothingModel);
                FormRenderHandler.renderVanillaModelPartWithTexture(arm == HumanoidArm.RIGHT ? clothingModel.rightArm : clothingModel.leftArm,
                        stackCorrector, matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                        light, 1F);
            }
        }
    }

    public static Supplier<AccessoryRenderer> of(ArmorModel armorModel, EquipmentSlot renderAs) {
        return () -> new SimpleClothingRenderer(armorModel, Set.of(new ModelComponent(armorModel, renderAs)));
    }

    public static Supplier<AccessoryRenderer> of(ArmorModel humanoidModel, Set<ModelComponent> components) {
        return () -> new SimpleClothingRenderer(humanoidModel, components);
    }
}
