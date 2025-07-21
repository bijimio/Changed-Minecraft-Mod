package net.ltxprogrammer.changed.client.latexparticles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class LatexParticle {
    protected static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0D);

    protected final Level level;
    private final int lifespan;

    private int age = 0;
    protected boolean stoppedByCollision;
    protected boolean isOnGround;
    protected boolean isOnWall;

    public LatexParticle(Level level, int lifespan) {
        this.level = level;
        this.lifespan = lifespan;
    }

    public void tick() {
        increaseAge();
    }

    public abstract ParticleRenderType getRenderType();

    public void setupForRender(PoseStack poseStack, float partialTicks) {
        this.setupForRender(poseStack, partialTicks, SetupContext.THIRD_PERSON);
    }

    public void setupForRender(PoseStack poseStack, float partialTicks, SetupContext setupContext) {}
    public abstract void renderFromEvent(VertexConsumer buffer, Camera camera, float partialTicks, SetupContext context);
    public abstract void renderFromLayer(MultiBufferSource buffer, float partialTicks);

    public int getAge() {
        return age;
    }

    public void increaseAge() {
        age++;
    }

    public boolean shouldExpire() {
        return age >= lifespan;
    }

    public boolean isForEntity(Entity entity) {
        return false;
    }

    public boolean shouldCull() {
        return true;
    }

    public abstract AABB getBoundingBox();

    public void onCollide() {}

    public boolean wantsPartInfo(ModelPart part) {
        return false;
    }

    public void handlePartPosition(ModelPart part, PoseStack.Pose modelPose) {

    }
}
