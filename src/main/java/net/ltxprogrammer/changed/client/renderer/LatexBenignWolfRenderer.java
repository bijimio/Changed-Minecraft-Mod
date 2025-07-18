package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexBenignWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexBenignWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBenignWolfRenderer extends AdvancedHumanoidRenderer<LatexBenignWolf, LatexBenignWolfModel, ArmorLatexMaleWolfModel<LatexBenignWolf>> {
    public LatexBenignWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBenignWolfModel(context.bakeLayer(LatexBenignWolfModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBenignWolf p_114482_) {
        return Changed.modResource("textures/latex_benign_wolf.png");
    }
}