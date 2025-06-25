package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SetTransfurVariantDataPacket implements ChangedPacket {
    private final int id;
    @Nullable
    private final List<SynchedEntityData.DataValue<?>> packedItems;

    private static void pack(List<SynchedEntityData.DataValue<?>> p_253940_, FriendlyByteBuf p_253901_) {
        for(SynchedEntityData.DataValue<?> datavalue : p_253940_) {
            datavalue.write(p_253901_);
        }

        p_253901_.writeByte(255);
    }

    private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf p_253726_) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

        int i;
        while((i = p_253726_.readUnsignedByte()) != 255) {
            list.add(SynchedEntityData.DataValue.read(p_253726_, i));
        }

        return list;
    }

    public SetTransfurVariantDataPacket(int id, List<SynchedEntityData.DataValue<?>> data) {
        this.id = id;
        this.packedItems = data;
    }

    public SetTransfurVariantDataPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.packedItems = unpack(buffer);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.id);
        pack(this.packedItems, buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            var player = UniversalDist.getLevel().getEntity(this.id);

            ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(player), variant -> {
                if (packedItems == null)
                    return;
                variant.getChangedEntity().getEntityData().assignValues(packedItems);
            });
            context.setPacketHandled(true);
        }
    }

    public int getId() {
        return this.id;
    }
}
