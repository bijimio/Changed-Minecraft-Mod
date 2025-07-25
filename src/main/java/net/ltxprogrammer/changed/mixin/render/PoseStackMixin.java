package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;
import java.util.function.Function;

@Mixin(PoseStack.class)
public abstract class PoseStackMixin implements PoseStackExtender {
    @Shadow @Final private Deque<PoseStack.Pose> poseStack;

    @Shadow public abstract void pushPose();

    @Shadow public abstract void popPose();

    @Shadow public abstract PoseStack.Pose last();

    @Override
    public <T> T popAndRepush(Function<PoseStack.Pose, T> fn) {
        PoseStack.Pose originalLast = this.poseStack.removeLast();
        this.pushPose(); // Makes a copy of the pose
        T returnValue = fn.apply(this.poseStack.getLast());
        this.popPose();
        this.poseStack.addLast(originalLast);
        return returnValue;
    }

    @Override
    public PoseStack.Pose first() {
        return poseStack.getFirst();
    }

    @Override
    public void setPose(PoseStack.Pose pose) {
        this.popPose();
        this.poseStack.addLast(pose);
    }

    @Override
    public void setPose(Matrix4f pose, Matrix3f normal) {
        var l = last();
        l.pose().set(pose);
        l.normal().set(normal);
    }

    @Override
    public PoseStack.Pose copyLast() {
        PoseStack nstack = new PoseStack();
        ((PoseStackExtender)nstack).setPose(last().pose(), last().normal());
        return nstack.last();
    }
}
