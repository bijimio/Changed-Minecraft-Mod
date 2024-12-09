package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.animation.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.network.packet.AnimationEventPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ChangedAnimationEvents {
    public static DeferredRegister<AnimationEvent<?>> REGISTRY = ChangedRegistry.ANIMATION_EVENTS.createDeferred(Changed.MODID);

    public static RegistryObject<AnimationEvent<TransfurAnimationParameters>> TRANSFUR_REPLICATE = register("transfur_replicate", TransfurAnimationParameters.CODEC);
    public static RegistryObject<AnimationEvent<TransfurAnimationParameters>> TRANSFUR_ABSORB = register("transfur_absorb", TransfurAnimationParameters.CODEC);

    public static RegistryObject<AnimationEvent<NoParameters>> STASIS_IDLE = register("stasis_idle", AnimationEvent.NO_PARAMETERS);
    public static RegistryObject<AnimationEvent<NoParameters>> SHOCK_STUN = register("shock_stun", AnimationEvent.NO_PARAMETERS);

    private static <T extends AnimationParameters> RegistryObject<AnimationEvent<T>> register(String name, Codec<T> parameters) {
        return REGISTRY.register(name, () -> new AnimationEvent<>(parameters));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEvent<T> event, @Nullable T parameters) {
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AnimationEventPacket<>(livingEntity.getId(), event, null, parameters, IntList.of(), List.of()));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEvent<T> event, AnimationCategory category, @Nullable T parameters) {
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AnimationEventPacket<>(livingEntity.getId(), event, category, parameters, IntList.of(), List.of()));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEventPacket<T> packet) {
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), packet);
    }

    public static void broadcastTransfurReplicateAnimation(LivingEntity livingEntity, TransfurVariant<?> variant, @Nullable LivingEntity source) {
        if (source != null)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                    AnimationEventPacket.Builder.of(livingEntity, TRANSFUR_REPLICATE.get(), AnimationCategory.TRANSFUR,
                            new TransfurAnimationParameters(variant)).addEntity(source).build());
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                    AnimationEventPacket.Builder.of(livingEntity, TRANSFUR_REPLICATE.get(), AnimationCategory.TRANSFUR,
                            new TransfurAnimationParameters(variant)).build());
    }

    public static void broadcastTransfurAbsorptionAnimation(LivingEntity livingEntity, TransfurVariant<?> variant, @NotNull LivingEntity source) {
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                AnimationEventPacket.Builder.of(livingEntity, TRANSFUR_ABSORB.get(), AnimationCategory.TRANSFUR,
                        new TransfurAnimationParameters(variant)).addEntity(source).build());
    }
}
