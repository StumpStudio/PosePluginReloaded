package ru.armagidon.poseplugin.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.PosePlugin;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.subsystems.PlayerHider;
import ru.armagidon.poseplugin.api.subsystems.implefine.Implementation;
import ru.armagidon.poseplugin.api.subsystems.implefine.UseImplementationFor;
import ru.armagidon.poseplugin.api.utility.LazyObject;
import ru.armagidon.poseplugin.bukkit.utilities.EquipmentPacketExtension;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityEquipment;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityMetadata;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EQUIPMENT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA;
import static ru.armagidon.poseplugin.api.utility.LazyObject.lazy;
import static ru.armagidon.poseplugin.bukkit.utilities.EquipmentPacketExtension.isModernPacketVersion;

//TODO implement BukkitPlayerHider
@UseImplementationFor(parent = PlayerHider.class)
public class BukkitPlayerHider extends PlayerHider<Player>
{


    @Implementation
    private static final LazyObject<BukkitPlayerHider> PLAYER_HIDER_INSTANCE = lazy(() -> new BukkitPlayerHider(PosePlugin.getPlugin(PosePlugin.class)), BukkitPlayerHider.class);
    private final HidingDaemon daemon;

    public BukkitPlayerHider(Plugin plugin) {
        daemon = new HidingDaemon(plugin);
        daemon.start();
    }

    @Override
    protected boolean handleHidePlayer(Player player) {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(player.getEntityId());
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(player).deepClone();
        byte data = (byte) (watcher.getByte(0) | (1 << 5));
        watcher.setObject(0, data, true);
        metadata.setMetadata(watcher.getWatchableObjects());
        metadata.broadcastPacket();

        final var container = new EquipmentPacketExtension()
                .setId(player.getEntityId())
                .fillWith(new ItemStack(Material.AIR))
                .packPackets();

        final var recipients = new HashSet<>(Bukkit.getOnlinePlayers());
        recipients.remove(player);
        recipients.forEach(container::send);

        daemon.add(player);

        return true;
    }

    @Override
    protected boolean handleShowPlayer(Player player) {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(player.getEntityId());
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(player);
        metadata.setMetadata(watcher.getWatchableObjects());
        metadata.broadcastPacket();

        final var container = new EquipmentPacketExtension()
                .setId(player.getEntityId())
                .fillWith(new ItemStack(Material.AIR))
                .packPackets();

        final var recipients = new HashSet<>(Bukkit.getOnlinePlayers());
        recipients.remove(player);
        recipients.forEach(container::send);

        daemon.remove(player);

        return true;
    }

    private static final class HidingDaemon extends PacketAdapter {

        private final Set<Player> hiddenPlayers;

        public HidingDaemon(Plugin plugin) {
            super(plugin, ENTITY_EQUIPMENT, ENTITY_METADATA);
            this.hiddenPlayers = ConcurrentHashMap.newKeySet(); //hiddenPlayers;
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            final var packet = event.getPacket();
            if (packet.getType().equals(ENTITY_EQUIPMENT)) {
                WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(packet);
                if (!(wrapper.getEntity(event.getPlayer().getWorld()) instanceof Player equipped)) return;
                if (!hiddenPlayers.contains(equipped)) return;

                //Player doesn't send these packets for themselves
                //if (event.getPlayer().equals(equipped)) return;

                if (isModernPacketVersion()) {
                    EquipmentPacketExtension extension = new EquipmentPacketExtension(wrapper);
                    extension.fillWith(new ItemStack(Material.AIR));
                    extension.packPackets().stream().findFirst().ifPresent(p -> event.setPacket(p.getHandle()));
                } else {
                    wrapper.setItem(new ItemStack(Material.AIR));
                }
            } else if (packet.getType().equals(ENTITY_METADATA)) {
                WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(packet);
                if (!(wrapper.getEntity(event.getPlayer().getWorld()) instanceof Player equiped)) return;
                if (!hiddenPlayers.contains(equiped)) return;

                WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(equiped).deepClone();
                byte data = (byte) (watcher.getByte(0) | (1 << 5));
                watcher.setObject(0, data, true);
                wrapper.setMetadata(watcher.getWatchableObjects());
                event.setPacket(wrapper.getHandle());

            }
        }

        private void start() {
            ProtocolLibrary.getProtocolManager().addPacketListener(this);
        }

        private void add(Player player ) {
            hiddenPlayers.add(player);
        }

        private void remove(Player player) {
            hiddenPlayers.remove(player);
        }

    }
}
