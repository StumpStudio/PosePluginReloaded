package ru.armagidon.poseplugin.bukkit.doppelganger;

import com.comphenix.protocol.wrappers.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.api.utility.datastructures.Pos;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityDestroy;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityMetadata;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerNamedEntitySpawn;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerPlayerInfo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitDoppelganger extends Doppelganger<Player, ItemStack>
{
    private final WrappedGameProfile profile;

    public BukkitDoppelganger(Player original) {
        super(original);
        this.profile = new WrappedGameProfile(getUuid(), "");
        this.profile.getProperties().putAll(WrappedGameProfile.fromPlayer(original).getProperties());
    }

    @Override
    public void broadcastSpawn() {
        NPCTracker.<Player>getInstance().getTrackersFor(this).forEach(this::render);
    }

    @Override
    public void broadcastDespawn() {
        NPCTracker.<Player>getInstance()
                .getTrackersFor(this)
                .forEach(this::unrender);
    }

    @Override
    public void render(Player receiver) {
        //Prepare player info
        final var playerInfoAdd = preparePlayerInfoAddition(this.profile);

        final var location = getOriginal().getLocation();
        //Prepare spawn packet
        final var spawn = prepareNPCSpawner(getEntityId(), getUuid(), location);

        //Prepare info removal
        final var removeInfo = preparePlayerInfoRemoval(this.profile);

        //Prepare basic metadata
        final var basicMetadata = prepareMetadata(WrappedDataWatcher.getEntityWatcher(getOriginal()));

        CompletableFuture
                .runAsync(() -> playerInfoAdd.sendPacket(receiver))
                .thenRun(() -> spawn.sendPacket(receiver))
                .thenRun(() -> removeInfo.sendPacket(receiver))
                .thenRun(() -> basicMetadata.sendPacket(receiver))
                .thenRun(() -> commandExecutor.runStartup(receiver));
    }

    @Override
    public void unrender(Player receiver) {
        final var destroy = new WrapperPlayServerEntityDestroy().setEntityIds(getEntityId());
        CompletableFuture
                .runAsync(() -> destroy.sendPacket(receiver))
                .thenRun(() -> {
                    try {
                        commandExecutor.runStop(receiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public Pos getPosition() {
        return new Pos(
                getOriginal().getWorld().getName(),
                getOriginal().getLocation().getX(),
                getOriginal().getLocation().getY(),
                getOriginal().getLocation().getZ());
    }


























    private static WrapperPlayServerPlayerInfo preparePlayerInfoAddition(WrappedGameProfile profile) {
        return new WrapperPlayServerPlayerInfo().setData(List.of(new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText("")))).setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    }

    private static WrapperPlayServerNamedEntitySpawn prepareNPCSpawner(int EID, UUID uuid, Location location) {
        return new WrapperPlayServerNamedEntitySpawn()
                .setEntityID(EID)
                .setPlayerUUID(uuid)
                .setX(location.getX())
                .setY(location.getY())
                .setZ(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch());
    }

    private static WrapperPlayServerPlayerInfo preparePlayerInfoRemoval(WrappedGameProfile profile) {
        return new WrapperPlayServerPlayerInfo().setData(List.of(new PlayerInfoData(
                profile,
                1,
                EnumWrappers.NativeGameMode.CREATIVE,
                WrappedChatComponent.fromLegacyText("")
        ))).setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
    }

    private WrapperPlayServerEntityMetadata prepareMetadata(WrappedDataWatcher original) {
        return new WrapperPlayServerEntityMetadata()
                .setEntityID(getEntityId())
                .setMetadata(original.getWatchableObjects());
    }
}
