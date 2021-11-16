package ru.armagidon.poseplugin.bukkit.doppelganger;

import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Direction;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Pose;
import ru.armagidon.poseplugin.api.utility.Pool;
import ru.armagidon.poseplugin.bukkit.wrappers.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static ru.armagidon.poseplugin.bukkit.utilities.SyncUtils.delay;

public class BukkitDoppelganger extends Doppelganger<Player, ItemStack, WrappedDataWatcher>
{
    public static final Pool<Player, Doppelganger<Player, ItemStack, ?>> DOPPELGANGER_POOL = new Pool<>(BukkitDoppelganger::new);

    private final WrappedGameProfile profile;

    public BukkitDoppelganger(Player original) {
        super(original);
        this.profile = new WrappedGameProfile(getUuid(), "");
        this.profile.getProperties().putAll(WrappedGameProfile.fromPlayer(original).getProperties());
        this.properties = new BukkitDoppelgangerProperties(original);
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
        final var basicMetadata = prepareMetadata(properties.getStorage());

        CompletableFuture
                .runAsync(() -> playerInfoAdd.sendPacket(receiver))
                .thenRun(() -> spawn.sendPacket(receiver))
                .thenRun(() -> removeInfo.sendPacket(receiver))
                .thenRun(() -> basicMetadata.sendPacket(receiver))
                .thenRun(() -> startBatch.run(receiver));
    }


    public void lay(Direction direction) {
        properties.setPose(Pose.SLEEPING);
        properties.setBedPosition(getPosition());

        final var metadata = prepareMetadata(this.properties.getStorage());

        Bed bed = (Bed) Bukkit.createBlockData(Material.WHITE_BED);
        bed.setPart(Bed.Part.HEAD);
        bed.setFacing(BlockFace.valueOf(direction.name()).getOppositeFace());
        final var bedPos = properties.getBedPosition();

        final var movePacket = new WrapperPlayServerRelEntityMoveLook()
                .setEntityID(getEntityId()).setDy(0).setDx(0).setDz(0);

        NPCTracker.<Player>getInstance().getTrackersFor(this).forEach(receiver -> {
            CompletableFuture
                    .runAsync(() -> metadata.sendPacket(receiver))
                    .thenRun(() -> movePacket.sendPacket(receiver));

            delay(() -> receiver.sendBlockChange(new Location(null, bedPos.x(), bedPos.y(), bedPos.z()), bed), 5L);
        });
    }

    @Override
    public void unrender(Player receiver) {
        final var destroy = new WrapperPlayServerEntityDestroy().setEntityIds(getEntityId());
        CompletableFuture
                .runAsync(() -> destroy.sendPacket(receiver))
                .thenRun(() -> endBatch.run(receiver));
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
