package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrowModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleBirdModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrow;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrowRenderer extends AdvancedHumanoidRenderer<LatexCrow, LatexCrowModel, ArmorLatexMaleBirdModel<LatexCrow>> {
    public LatexCrowRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrowModel(context.bakeLayer(LatexCrowModel.LAYER_LOCATION)), ArmorLatexMaleBirdModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrow entity) {
        return Changed.modResource("textures/latex_crow.png");
    }
}