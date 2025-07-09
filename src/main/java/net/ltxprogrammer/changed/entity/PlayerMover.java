package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.DuctBlock;
import net.ltxprogrammer.changed.entity.latex.LatexSwimMover;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public abstract class PlayerMover<T extends PlayerMoverInstance<?>> {
    public abstract T createInstance();

    private static class DefaultMover extends PlayerMover<DefaultMover.DefaultInstance> {
        private static class DefaultInstance extends PlayerMoverInstance<DefaultMover> {
            public DefaultInstance(DefaultMover parent) {
                super(parent);
            }

            @Override
            public void aiStep(Player player, InputWrapper input, LogicalSide side) {

            }

            @Override
            public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {

            }

            @Override
            public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
                return true;
            }
        }

        @Override
        public DefaultInstance createInstance() {
            return new DefaultInstance(this);
        }
    }

    public static DeferredRegister<PlayerMover<?>> REGISTRY = ChangedRegistry.PLAYER_MOVER.createDeferred(Changed.MODID);

    public static RegistryObject<DefaultMover> DEFAULT_MOVER = REGISTRY.register("default", DefaultMover::new);
    public static RegistryObject<DuctBlock.DuctMover> DUCT_MOVER = REGISTRY.register("duct", DuctBlock.DuctMover::new);
    public static RegistryObject<LatexSwimMover> LATEX_SWIM = REGISTRY.register("latex_swim", LatexSwimMover::new);
}
