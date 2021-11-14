package ru.armagidon.poseplugin.bukkit.doppelganger;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.*;

import java.util.Optional;

public class BukkitDoppelgangerProperties extends DoppelgangerProperties<Player, WrappedDataWatcher>
{

    private static final int POSE_INDEX = 6;
    private static final int ENTITY_TAGS_INDEX = 0;
    private final int indexDifference;

    public BukkitDoppelgangerProperties(Player source) {
        super(source, WrappedDataWatcher.getEntityWatcher(source).deepClone());
        var diff = 0;
        var success = true;
        do {
            var v = storage.getWatchableObject(13 + diff).getValue();
            success = v instanceof Optional;
            if (!success) diff++;
            if (diff >= 30)
                throw new RuntimeException("This version is not supported");
        } while (!success);
        this.indexDifference = diff;
    }

    @Override
    public void setPose(Pose pose) {
        synchronized (storage) {
            storage.setObject(POSE_INDEX, Registry.get(EnumWrappers.getEntityPoseClass()), EntityPose.values()[pose.ordinal()].toNms());
        }
    }

    @Override
    public void setBedPosition(Doppelganger.Pos pos) {
        synchronized (storage) {
            final var world = Bukkit.getWorld(pos.world());
            var minHeight = 0;
            try {
                minHeight = world.getMinHeight();
            } catch (Throwable ignored) {}
            final var bedPos = pos.setY(minHeight);
            BlockPosition bp = new BlockPosition((int) bedPos.x(), (int) bedPos.y(), (int) bedPos.z());
            storage.setObject(13 + indexDifference, Registry.getBlockPositionSerializer(true), Optional.of(BlockPosition.getConverter().getGeneric(bp)));
        }
    }

    @Override
    public void setInvisible(boolean invisibility) {
        synchronized (storage) {
            final var tags = storage.getByte(ENTITY_TAGS_INDEX);
            storage.setObject(ENTITY_TAGS_INDEX, Registry.get(Byte.class), setBit(tags, 5, invisibility));
        }
    }

    @Override
    public void setOverlays(byte overlays) {
         synchronized (storage) {
             storage.setObject(16 + indexDifference, Registry.get(Byte.class), overlays);
         }
    }

    @Override
    public void setActiveHand(Hand hand) {
        if (!isHandActivated())
            activateHand();

        storage.setObject(17 + indexDifference, Registry.get(Byte.class), hand.getFlag() ? 255 : 0);
    }

    @Override
    public void activateHand() {
        if (isHandActivated()) return;
        final var data = storage.getByte(7 + indexDifference);
        storage.setObject(7 + indexDifference, Registry.get(Byte.class), setBit(setBit(data, 1, false), 0, true));
    }

    @Override
    public void deactivateHand() {
        if (!isHandActivated()) return;
        final var data = storage.getByte(7 + indexDifference);
        storage.setObject(7 + indexDifference, Registry.get(Byte.class), setBit(setBit(data, 1, false), 0, false));
    }

    @Override
    public void setRiptide(boolean riptideState) {
        final var data = storage.getByte(7 + indexDifference);
        storage.setObject(7 + indexDifference, Registry.get(Byte.class), setBit(data, 2, riptideState));
    }

    @Override
    public Pose getPose() {
        return Pose.values()[EnumWrappers.getEntityPoseConverter().getSpecific(storage.getObject(POSE_INDEX)).ordinal()];
    }

    @Override
    public Doppelganger.Pos getBedPosition() {
        Optional<BlockPosition> position = ((Optional<?>) storage.getObject(13 + indexDifference)).map(o -> BlockPosition.getConverter().getSpecific(o));
        final var pos = position.get();
        return new Doppelganger.Pos("", pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public boolean isInvisible() {
        return (storage.getByte(ENTITY_TAGS_INDEX) & 0x20) != 0;
    }

    @Override
    public byte getOverlays() {
        return storage.getByte(16 + indexDifference);
    }

    @Override
    public Hand getActiveHand() {
        return storage.getByte(17 + indexDifference) > 0 ? Hand.MAIN : Hand.OFF;
    }

    @Override
    public boolean isHandActivated() {
        final var data = storage.getByte(7 + indexDifference);
        return (data & 0x1) != 0 && (data & (0x2)) != 0;
    }

    @Override
    public boolean isRiptiding() {
        return (storage.getByte(7 + indexDifference) & 0x04) != 0;
    }

    private static byte setBit(int n, int k, boolean set) {
        if (set) {
            return (byte) (n | (1 << k));
        } else {
            return (byte) (n & ~(1 << k));
        }
    }
}
