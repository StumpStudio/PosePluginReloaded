package ru.armagidon.poseplugin.bukkit.doppelganger;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Pose;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityDestroy;
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
    public void spawn() {
        NPCTracker.<Player>getInstance().getTrackersFor(this).forEach(this::render);
    }

    @Override
    public void despawn() {
        NPCTracker.<Player>getInstance()
                .getTrackersFor(this)
                .forEach(this::unrender);
    }

    @Override
    public void render(Player receiver) {
        //Prepare player info
        final var playerInfoAdd = addPlayerInfo(this.profile);

        final var location = getOriginal().getLocation();
        //Prepare spawn packet
        final var spawn = spawnNPC(getEntityId(), getUuid(), location);

        //Prepare info removal
        final var removeInfo = removePlayerInfo(this.profile);

        CompletableFuture
                .runAsync(() -> playerInfoAdd.sendPacket(receiver))
                .thenRun(() -> spawn.sendPacket(receiver))
                .thenRun(() -> removeInfo.sendPacket(receiver));
    }

    @Override
    public void unrender(Player receiver) {
        final var destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(getEntityId());
        CompletableFuture.runAsync(() -> destroy.sendPacket(receiver));
    }

    @Override
    public double getX() {
        return getOriginal().getLocation().getX();
    }

    @Override
    public double getY() {
        return getOriginal().getLocation().getY();
    }

    @Override
    public double getZ() {
        return getOriginal().getLocation().getZ();
    }

    @Override
    protected void handlePoseChange(Pose old, Pose newPose) {

    }














    private static WrapperPlayServerPlayerInfo addPlayerInfo(WrappedGameProfile profile) {
        return new WrapperPlayServerPlayerInfo().setData(List.of(new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromLegacyText("")))).setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    }

    private static WrapperPlayServerNamedEntitySpawn spawnNPC(int EID, UUID uuid, Location location) {
        return new WrapperPlayServerNamedEntitySpawn()
                .setEntityID(EID)
                .setPlayerUUID(uuid)
                .setX(location.getX())
                .setY(location.getY())
                .setZ(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch());
    }

    private static WrapperPlayServerPlayerInfo removePlayerInfo(WrappedGameProfile profile) {
        return new WrapperPlayServerPlayerInfo().setData(List.of(new PlayerInfoData(
                profile,
                1,
                EnumWrappers.NativeGameMode.CREATIVE,
                WrappedChatComponent.fromLegacyText("")
        ))).setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
    }
}
