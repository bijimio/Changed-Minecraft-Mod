package net.ltxprogrammer.changed.util;

import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.ltxprogrammer.changed.client.ClientLevelExtension;
import net.ltxprogrammer.changed.client.LocalTransfurVariantInstance;
import net.ltxprogrammer.changed.client.RemoteTransfurVariantInstance;
import net.ltxprogrammer.changed.client.gui.GluBlockEditScreen;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.server.ServerTransfurVariantInstance;
import net.ltxprogrammer.changed.world.LevelExtension;
import net.ltxprogrammer.changed.world.ServerLevelExtension;
import net.ltxprogrammer.changed.world.WorldGenRegionLevelExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public class UniversalDist {
    public static class ClientDist {
        public static Level getLevel() {
            return Minecraft.getInstance().level;
        }
        public static Executor getSidedExecutor() {
            return Minecraft.getInstance();
        }
        public static Player getLocalPlayer() {
            return Minecraft.getInstance().player;
        }
        public static void displayClientMessage(Component component, boolean notInChat) {
            Minecraft.getInstance().player.displayClientMessage(component, notInChat);
        }
        public static Entity getCameraEntity() {
            return Minecraft.getInstance().cameraEntity;
        }
        public static HitResult getLocalHitResult() {
            return Minecraft.getInstance().hitResult;
        }
        public static void openGluBlock(Player player, GluBlockEntity gluBlockEntity) {
            if (player == getLocalPlayer())
                Minecraft.getInstance().setScreen(new GluBlockEditScreen(gluBlockEntity));
        }

        public static TransfurVariantInstance<?> createVariantFor(TransfurVariant<?> variant, @NotNull Player host) {
            if (host instanceof LocalPlayer localPlayer)
                return new LocalTransfurVariantInstance<>(variant, localPlayer);
            else if (host instanceof RemotePlayer remotePlayer)
                return new RemoteTransfurVariantInstance<>(variant, remotePlayer);
            else
                return null;
        }

        public static LevelExtension getLevelExtension(LevelAccessor level) {
            if (level instanceof ClientLevel)
                return ClientLevelExtension.INSTANCE;
            if (level instanceof ServerLevel)
                return ServerLevelExtension.INSTANCE;
            return LevelExtension.INSTANCE;
        }
    }

    public static class ServerDist {
        public static Level getLevel() {
            return ServerLifecycleHooks.getCurrentServer().overworld();
        }

        public static TransfurVariantInstance<?> createVariantFor(TransfurVariant<?> variant, @NotNull Player host) {
            if (host instanceof ServerPlayer serverPlayer)
                return new ServerTransfurVariantInstance<>(variant, serverPlayer);
            return null;
        }

        public static LevelExtension getLevelExtension(LevelAccessor level) {
            if (level instanceof ServerLevel)
                return ServerLevelExtension.INSTANCE;
            if (level instanceof WorldGenRegion)
                return WorldGenRegionLevelExtension.INSTANCE;
            return LevelExtension.INSTANCE;
        }
    }

    @Deprecated
    public static Level getLevel() {
        Level level = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLevel);
        return level != null ? level :
                DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> ServerDist::getLevel);
    }

    public static Level getLevel(NetworkEvent.Context context) {
        if (context.getSender() != null)
            return context.getSender().level();
        else
            return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLevel);
    }

    /**
     * Gets the Changed logical extension for a level. Created to run side dependant code that may vary for a level type.
     * @param level level to extend logically
     * @return level extension instance to apply logic to
     */
    public static LevelExtension getLevelExtension(LevelAccessor level) {
        LevelExtension levelExtension = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientDist.getLevelExtension(level));
        return levelExtension != null ? levelExtension : ServerDist.getLevelExtension(level);
    }

    public static void displayClientMessage(Component component, boolean notInChat) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDist.displayClientMessage(component, notInChat));
    }

    public static @Nullable Player getLocalPlayer() {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLocalPlayer);
    }

    public static @Nullable Entity getCameraEntity() {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getCameraEntity);
    }

    public static boolean isClientRemotePlayer(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.level().isClientSide && !isLocalPlayer(player);
        }

        return false;
    }

    public static boolean isLocalPlayer(LivingEntity entity) {
        return entity == getLocalPlayer();
    }

    public static boolean isLocalPlayer(Player player) {
        return player == getLocalPlayer();
    }

    public static HitResult getLocalHitResult() {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLocalHitResult);
    }

    public static void openGluBlock(Player player, GluBlockEntity gluBlockEntity) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDist.openGluBlock(player, gluBlockEntity));
    }

    @Nullable
    public static TransfurVariantInstance<?> createVariantFor(@NotNull TransfurVariant<?> variant, @NotNull Player host) {
        if (host instanceof ServerPlayer)
            return ServerDist.createVariantFor(variant, host);
        else
            return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientDist.createVariantFor(variant, host));
    }
}
