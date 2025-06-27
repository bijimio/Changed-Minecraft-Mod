package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface ChangedPacket {
    void write(FriendlyByteBuf buffer);
    void handle(Supplier<NetworkEvent.Context> contextSupplier);

    default boolean canBeHandled(Supplier<NetworkEvent.Context> contextSupplier) {
        return UniversalDist.getLevel() != null;
    }
}
