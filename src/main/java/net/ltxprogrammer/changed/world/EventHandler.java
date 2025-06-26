package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public abstract class EventHandler {
    @SubscribeEvent
    public static void onEmptySwingEvent(PlayerInteractEvent.LeftClickEmpty event) {
        AccessorySlots.getForEntity(event.getEntity()).ifPresent(slots -> slots.onEntitySwing(event.getHand()));
    }

    @SubscribeEvent
    public static void onEntityAttackedEvent(LivingAttackEvent event) {
        AccessorySlots.getForEntity(event.getEntity()).ifPresent(slots -> slots.onEntityDamage(event.getSource(), event.getAmount()));
    }
}
