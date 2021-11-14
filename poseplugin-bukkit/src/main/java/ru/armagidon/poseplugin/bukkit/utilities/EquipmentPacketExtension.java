package ru.armagidon.poseplugin.bukkit.utilities;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityEquipment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EquipmentPacketExtension
{

    private final Map<EquipmentSlot, ItemStack> slots = new ConcurrentHashMap<>();
    private final boolean modernPacketVersion;
    private boolean packed;
    private int id;

    private PacketContainer<WrapperPlayServerEntityEquipment> container;

    public EquipmentPacketExtension() {
        this.modernPacketVersion = isModernPacketVersion();
    }

    public EquipmentPacketExtension(WrapperPlayServerEntityEquipment source) {
        this();
        if (modernPacketVersion) {
            source.getSlotStackPairs().forEach(pair ->
                    slots.put(EquipmentSlot.values()[pair.getFirst().ordinal()], pair.getSecond()));
        } else {
            slots.put(EquipmentSlot.values()[source.getSlot().ordinal()], source.getItem());
        }
    }

    public EquipmentPacketExtension setId(int id) {
        this.id = id;
        return this;
    }

    public EquipmentPacketExtension fillWith(ItemStack item) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            setItem(slot, item);
        }
        return this;
    }

    public EquipmentPacketExtension setItem(EquipmentSlot slot, ItemStack stack) {
        Validate.notNull(slot);
        Validate.notNull(stack);
        slots.put(slot, stack);
        packed = false;
        return this;
    }

    public void sendPacket(Player player) {
        if (!packed) {
            pack();
        }
        container.send(player);
    }

    public PacketContainer<WrapperPlayServerEntityEquipment> packPackets() {
        if (!packed) pack();
        return container;
    }

    private void pack() {
        if (modernPacketVersion) {
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> slots = this.slots.entrySet().stream().map(entry ->
                    new Pair<>(EnumWrappers.ItemSlot.values()[entry.getKey().ordinal()], entry.getValue())).collect(Collectors.toList());

            if (slots.isEmpty()) {
                container = new PacketContainer<>();
                return;
            }
            WrapperPlayServerEntityEquipment updatePacket = new WrapperPlayServerEntityEquipment()
                    .setEntityID(id)
                    .setSlotStackPairsList(slots);
            container = new PacketContainer<>(updatePacket);
        } else {
            container = slots.entrySet().stream().
                    map(entry -> new WrapperPlayServerEntityEquipment()
                            .setEntityID(id)
                            .setSlot(EnumWrappers.ItemSlot.values()[entry.getKey().ordinal()])
                            .setItem(entry.getValue())).collect(Collectors.collectingAndThen(Collectors.toList(), PacketContainer::new));
        }
        packed = true;
    }

    public static boolean isModernPacketVersion() {
        try {
            new WrapperPlayServerEntityEquipment().setItem(new ItemStack(Material.AIR));
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
