package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LocalTransfurVariantInstance<T extends ChangedEntity> extends ClientTransfurVariantInstance<T> {
    private final LocalPlayer host;

    public LocalTransfurVariantInstance(TransfurVariant<T> parent, LocalPlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    protected void tickTransfurProgress() {
        super.tickTransfurProgress();

        if (transfurProgression < 1f || this.ageAsVariant < 30 || !this.getItemUseMode().holdMainHand || GrabEntityAbility.getControllingEntity(this.host) != this.host) {
            ((LocalPlayerAccessor)host).setHandsBusy(true);
        } else if (host.getVehicle() == null && host.isHandsBusy()) {
            ((LocalPlayerAccessor)host).setHandsBusy(false);
        }
    }

    @Override
    public void unhookAll(Player player) {
        super.unhookAll(player);

        if (host.getVehicle() == null && host.isHandsBusy()) {
            ((LocalPlayerAccessor)host).setHandsBusy(false);
        }
    }
}
