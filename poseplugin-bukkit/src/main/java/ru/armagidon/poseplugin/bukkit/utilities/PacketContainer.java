package ru.armagidon.poseplugin.bukkit.utilities;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.bukkit.wrappers.AbstractPacket;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public final class PacketContainer<T extends AbstractPacket> {

    private final T[] packets;

    @SuppressWarnings("unchecked")
    public PacketContainer(Collection<T> packets) {
        this((T[]) packets.toArray(AbstractPacket[]::new));
    }

    @SafeVarargs
    public PacketContainer(T... packets){
        this.packets = packets;
    }

    public void send(Player receiver) {
        for (T packet : packets) {
            packet.sendPacket(receiver);
        }
    }

    public void broadcast() {
        Arrays.stream(packets).map(AbstractPacket::getHandle).forEach(p -> ProtocolLibrary.getProtocolManager().broadcastServerPacket(p));
    }


    public Stream<T> stream() {
        return Arrays.stream(packets);
    }
}
