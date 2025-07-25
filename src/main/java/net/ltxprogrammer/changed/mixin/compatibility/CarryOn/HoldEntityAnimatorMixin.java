package net.ltxprogrammer.changed.mixin.compatibility.CarryOn;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.AbstractUpperBodyAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.HoldEntityAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.Constants;
import tschipp.carryon.client.render.CarryRenderHelper;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;
import tschipp.carryon.common.scripting.CarryOnScript;

@Mixin(value = HoldEntityAnimator.class, remap = false)
@RequiredMods("carryon")
public abstract class HoldEntityAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public HoldEntityAnimatorMixin(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void maybeSetupCarryOn(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        // Copied from RenderEvents
        if (Constants.CLIENT_CONFIG.renderArms && entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player) {
            CarryOnData carry = CarryOnDataManager.getCarryData(player);
            if (carry.isCarrying() && !player.isVisuallySwimming() && !player.isFallFlying()) {
                boolean sneaking = !player.getAbilities().flying && player.isShiftKeyDown() || player.isCrouching();
                float x = 1.0F + (sneaking ? 0.2F : 0.0F) + (carry.isCarrying(CarryOnData.CarryType.BLOCK) ? 0.0F : 0.3F);
                float z = 0.05F;
                float width = CarryRenderHelper.getRenderWidth(player);
                float offset = Math.min((width - 1.0F) / 1.5F, 0.2F);
                if (carry.getActiveScript().isPresent()) {
                    CarryOnScript.ScriptRender render = ((CarryOnScript)carry.getActiveScript().get()).scriptRender();
                    boolean renderLeft = render.renderLeftArm();
                    boolean renderRight = render.renderRightArm();
                    Vec3 rotLeft = render.renderRotationLeftArm().getVec((double)(-x), (double)(-offset), (double)z);
                    Vec3 rotRight = render.renderRotationRightArm().getVec((double)(-x), (double)offset, (double)(-z));
                    if (renderLeft) {
                        this.changeRotation(this.leftArm, (float)rotLeft.x, (float)rotLeft.y, (float)rotLeft.z);
                    }

                    if (renderRight) {
                        this.changeRotation(this.rightArm, (float)rotRight.x, (float)rotRight.y, (float)rotRight.z);
                    }
                } else {
                    this.changeRotation(this.rightArm, -x, offset, -z);
                    this.changeRotation(this.leftArm, -x, -offset, z);
                }
            }
        }
    }

    @Unique
    private void changeRotation(ModelPart part, float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }
}
