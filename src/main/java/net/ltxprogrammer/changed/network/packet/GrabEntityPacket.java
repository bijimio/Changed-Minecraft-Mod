package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class GrabEntityPacket implements ChangedPacket {
    public enum GrabType {
        /**
         * Target will be release by the latex
         */
        RELEASE,
        /**
         * Target is grabbed by latex by their arms
         */
        ARMS,
        /**
         * Target is fully encased by latex entity
         */
        SUIT
    }

    public final int sourceEntity;
    public final int targetEntity;
    public final GrabType type;

    public GrabEntityPacket(LivingEntity source, LivingEntity target, GrabType type) {
        this.sourceEntity = source.getId();
        this.targetEntity = target.getId();
        this.type = type;
    }

    public GrabEntityPacket(FriendlyByteBuf buffer) {
        this.sourceEntity = buffer.readInt();
        this.targetEntity = buffer.readInt();
        this.type = GrabType.values()[buffer.readInt()];
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(sourceEntity);
        buffer.writeInt(targetEntity);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var source = level.getEntity(sourceEntity);
                var target = level.getEntity(targetEntity);

                if (!(source instanceof LivingEntity livingSource)) return;
                if (!(target instanceof LivingEntity livingTarget)) return;

                ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(livingSource), variant -> {
                    variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), ability -> {
                        switch (type) {
                            case RELEASE -> ability.releaseEntity();
                            case SUIT -> {
                                if (livingTarget instanceof Player && !Changed.config.server.isGrabEnabled.get())
                                    return;

                                ability.suitEntity(livingTarget);
                            }
                            case ARMS -> {
                                if (livingTarget instanceof Player && !Changed.config.server.isGrabEnabled.get())
                                    return;

                                ability.grabEntity(livingTarget);
                            }
                        }
                    });
                });
            });
        }

        else {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var sender = context.getSender();
                var target = level.getEntity(targetEntity);
                if (!(target instanceof LivingEntity livingTarget))
                    return;
                if (!target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) && !(target instanceof Player))
                    return;
                context.setPacketHandled(true);
                if (sender.getId() == sourceEntity) {
                    if (ProcessTransfur.isPlayerNotLatex(sender))
                        return; // Invalid, sender has to be latex
                } else {
                    return; // Invalid, sender cannot dictate other entities grab action
                }

                ProcessTransfur.ifPlayerTransfurred(sender, variant -> {
                    var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability == null)
                        return;

                    switch (type) {
                        case RELEASE -> {
                            boolean wasSuited = ability.suited;
                            ability.releaseEntity();
                            ChangedSounds.broadcastSound(sender, wasSuited ? ChangedSounds.POISON : ChangedSounds.BLOW1, 1.0f, 1.0f);
                        }
                        case SUIT -> {
                            if (livingTarget instanceof Player && !Changed.config.server.isGrabEnabled.get())
                                return;

                            ChangedSounds.broadcastSound(sender, ChangedSounds.POISON, 1.0f, 1.0f);
                            ability.suitEntity(livingTarget);
                        }
                        case ARMS -> {
                            if (livingTarget instanceof Player && !Changed.config.server.isGrabEnabled.get())
                                return;

                            boolean wasSuited = ability.suited;
                            ability.grabEntity(livingTarget);
                            ChangedSounds.broadcastSound(sender, wasSuited ? ChangedSounds.POISON : ChangedSounds.BLOW1, 1.0f, 1.0f);
                        }
                    }
                });

                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this); // Echo
            });
        }
    }

    public static GrabEntityPacket release(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.RELEASE);
    }

    public static GrabEntityPacket initialGrab(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.ARMS);
    }

    public static GrabEntityPacket suitGrab(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.SUIT);
    }

    public static class GrabKeyState implements ChangedPacket {
        private final UUID uuid;
        private final boolean attackKey;
        private final boolean useKey;

        public GrabKeyState(Player player, boolean attackKey, boolean useKey) {
            this.uuid = player.getUUID();
            this.attackKey = attackKey;
            this.useKey = useKey;
        }

        public GrabKeyState(FriendlyByteBuf buffer) {
            this.uuid = buffer.readUUID();
            this.attackKey = buffer.readBoolean();
            this.useKey = buffer.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeUUID(this.uuid);
            buffer.writeBoolean(this.attackKey);
            buffer.writeBoolean(this.useKey);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    Player player = level.getPlayerByUUID(this.uuid);
                    if (player == null)
                        throw new IllegalArgumentException("Cannot get player of uuid " + uuid);
                    ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                        variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), instance -> {
                            instance.attackDown = this.attackKey;
                            instance.useDown = this.useKey;
                        });
                    });
                });
            }

            else {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    var sender = context.getSender();
                    ProcessTransfur.ifPlayerTransfurred(sender, variant -> {
                        variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), instance -> {
                            instance.attackDown = this.attackKey;
                            instance.useDown = this.useKey;
                            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this);
                        });
                    });
                });
            }
        }
    }

    public static class AnnounceEscapeKey implements ChangedPacket {
        /** Escapee's UUID */
        private final UUID uuid;
        private final AbstractAbilityInstance.KeyReference keyReference;

        public AnnounceEscapeKey(Player player, AbstractAbilityInstance.KeyReference keyReference) {
            this.uuid = player.getUUID();
            this.keyReference = keyReference;
        }

        public AnnounceEscapeKey(FriendlyByteBuf buffer) {
            this.uuid = buffer.readUUID();
            this.keyReference = buffer.readEnum(AbstractAbilityInstance.KeyReference.class);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeUUID(this.uuid);
            buffer.writeEnum(this.keyReference);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    var player = level.getPlayerByUUID(this.uuid);
                    if (!(player instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() == null)
                        throw new IllegalStateException("Player is not grabbed");

                    var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability != null)
                        ability.currentEscapeKey = this.keyReference;
                    else
                        throw new IllegalStateException("Grabber does not have grab ability");
                });
            }

            return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
        }
    }

    public static class EscapeKeyState implements ChangedPacket {
        /** Escapee's UUID */
        private final UUID uuid;
        private final boolean keyForward;
        private final boolean keyBackward;
        private final boolean keyLeft;
        private final boolean keyRight;

        public EscapeKeyState(Player player, boolean keyForward, boolean keyBackward, boolean keyLeft, boolean keyRight) {
            this.uuid = player.getUUID();
            this.keyForward = keyForward;
            this.keyBackward = keyBackward;
            this.keyLeft = keyLeft;
            this.keyRight = keyRight;
        }

        public EscapeKeyState(FriendlyByteBuf buffer) {
            this.uuid = buffer.readUUID();
            this.keyForward = buffer.readBoolean();
            this.keyBackward = buffer.readBoolean();
            this.keyLeft = buffer.readBoolean();
            this.keyRight = buffer.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeUUID(this.uuid);
            buffer.writeBoolean(this.keyForward);
            buffer.writeBoolean(this.keyBackward);
            buffer.writeBoolean(this.keyLeft);
            buffer.writeBoolean(this.keyRight);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    if (!(level.getPlayerByUUID(this.uuid) instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() == null)
                        throw new IllegalStateException("Player is not grabbed");

                    var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability != null) {
                        ability.escapeKeyForward = this.keyForward;
                        ability.escapeKeyBackward = this.keyBackward;
                        ability.escapeKeyLeft = this.keyLeft;
                        ability.escapeKeyRight = this.keyRight;
                    }

                    else
                        throw new IllegalStateException("Grabber does not have grab ability");
                });
            }

            else {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    final var entity = context.getSender();
                    if (!(entity instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() == null)
                        throw new IllegalStateException("Player is not grabbed");

                    var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability != null) {
                        ability.escapeKeyForward = this.keyForward;
                        ability.escapeKeyBackward = this.keyBackward;
                        ability.escapeKeyLeft = this.keyLeft;
                        ability.escapeKeyRight = this.keyRight;
                        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), this);
                    }

                    else
                        throw new IllegalStateException("Grabber does not have grab ability");
                });
            }
        }
    }

    public static GrabKeyState keyState(Player player, boolean attack, boolean use) {
        return new GrabKeyState(player, attack, use);
    }
}
