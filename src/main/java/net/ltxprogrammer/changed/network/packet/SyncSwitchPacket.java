package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.client.gui.InfuserScreen;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.GuiStateProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SyncSwitchPacket implements ChangedPacket {
    private final int containerId;
    private final boolean state;
    private final ResourceLocation name;

    public SyncSwitchPacket(InfuserScreen.Switch switchWidget) {
        containerId = switchWidget.containerScreen.getMenu().containerId;
        state = switchWidget.selected();
        name = switchWidget.getName();
    }

    public SyncSwitchPacket(FriendlyByteBuf buffer) {
        containerId = buffer.readInt();
        state = buffer.readBoolean();
        name = buffer.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(containerId);
        buffer.writeBoolean(state);
        buffer.writeResourceLocation(name);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        final var player = Objects.requireNonNullElseGet(context.getSender(), UniversalDist::getLocalPlayer);
        if (player.containerMenu.containerId == containerId && player.containerMenu instanceof GuiStateProvider menu) {
            menu.getState().put(name.toString(), state);
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.failedFuture(new IllegalStateException());
    }

    public static SyncSwitchPacket of(InfuserScreen.Switch switchWidget) {
        return new SyncSwitchPacket(switchWidget);
    }
}
