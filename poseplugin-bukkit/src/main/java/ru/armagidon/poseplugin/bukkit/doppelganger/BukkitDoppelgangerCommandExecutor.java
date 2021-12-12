package ru.armagidon.poseplugin.bukkit.doppelganger;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.DoppelgangerCommandExecutor;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.MetadataWrapper;
import ru.armagidon.poseplugin.api.utility.datastructures.Pair;
import ru.armagidon.poseplugin.api.utility.datastructures.StorageWithVaryingMutability;
import ru.armagidon.poseplugin.api.utility.enums.Direction;
import ru.armagidon.poseplugin.api.utility.enums.Pose;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityMetadata;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerRelEntityMoveLook;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BukkitDoppelgangerCommandExecutor extends DoppelgangerCommandExecutor<Player>
{

    private static final String ID = "id";
    private static final String POSITION = "position";
    private static final String ORIGINAL_DW = "original-dw";
    private static final String DIRECTION = "direction";
    private static final String BED_POS = "bed-pos";
    private static final String RECEIVER = "receiver";

    private final Doppelganger<Player, ItemStack> doppelganger;

    private static final Consumer<StorageWithVaryingMutability> LAY_COMMAND = commandData -> {
            final int EID = commandData.<Integer>get(ID);
            final Direction direction = commandData.get(DIRECTION);
            final var properties = commandData.<MetadataWrapper<WrappedDataWatcher>>get(ORIGINAL_DW);

            properties.setPose(Pose.SLEEPING);
            properties.setBedPosition(commandData.get(POSITION));

            final var bedPos = properties.getBedPosition();
            commandData.putImmutableVariable(BED_POS, bedPos);

            Bed bed = (Bed) Bukkit.createBlockData(Material.WHITE_BED);
            bed.setFacing(BlockFace.valueOf(direction.name()));
            bed.setPart(Bed.Part.HEAD);
            final var loc = new Location(null, (int) bedPos.x(), (int) bedPos.y(), (int) bedPos.z());
//        final var bedPacket = new WrapperPlayServerBlockChange()
//                .setBlockData(WrappedBlockData.createData(Material.WHITE_BED, (0x8 | direction.ordinal())))
//                .setLocation(new BlockPosition((int) bedPos.x(), (int) bedPos.y(), (int) bedPos.z()));

            final var movePacket = new WrapperPlayServerRelEntityMoveLook()
                    .setEntityID(EID).setDy(0).setDx(0).setDz(0);
            final var metadata = new WrapperPlayServerEntityMetadata()
                    .setMetadata(properties.getStorage().getWatchableObjects())
                    .setEntityID(EID);
            final var receiver = commandData.<Player>get(RECEIVER);

            CompletableFuture
                    .runAsync(() -> metadata.sendPacket(receiver))
                    .thenRun(() -> movePacket.sendPacket(receiver))
                    .thenRun(() -> receiver.sendBlockChange(loc, bed));
    };
    private static final Consumer<StorageWithVaryingMutability> CLEAN_BEDPOSITION = commandData -> {
        final var properties = commandData.<MetadataWrapper<WrappedDataWatcher>>get(ORIGINAL_DW);
        final var bedPos = properties.getBedPosition();
        final var receiver = commandData.<Player>get(RECEIVER);
        final var loc = new Location(receiver.getWorld(), (int) bedPos.x(), (int) bedPos.y(), (int) bedPos.z());
        synchronized (loc.getBlock()) {
            receiver.sendMessage(loc.toString());
            receiver.sendBlockChange(loc, loc.getBlock().getBlockData());
        }
    };


    public BukkitDoppelgangerCommandExecutor(Doppelganger<Player, ItemStack> doppelganger) {
        this.doppelganger = doppelganger;
    }

    @Override
    public void lay(Direction direction) {
        MetadataWrapper<WrappedDataWatcher> properties = new BukkitMetadataWrapper(WrappedDataWatcher.getEntityWatcher(doppelganger.getOriginal()).deepClone());
        synchronized (startUpCommands) {
            final var commandData = new StorageWithVaryingMutability();
            commandData.putMutableVariable(ID, doppelganger.getEntityId());
            commandData.putImmutableVariable(POSITION, doppelganger.getPosition());
            commandData.putImmutableVariable(ORIGINAL_DW, properties);
            commandData.putImmutableVariable(DIRECTION, direction);
            startUpCommands.put("lay-command", Pair.of(commandData, LAY_COMMAND));
        }
        synchronized (endCommands) {
            final var commandData = new StorageWithVaryingMutability();
            commandData.putImmutableVariable(ORIGINAL_DW, properties);
            endCommands.put("bedpos-clean", Pair.of(commandData, CLEAN_BEDPOSITION));
        }
    }
}
