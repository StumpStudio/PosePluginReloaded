package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import lombok.Getter;
import ru.armagidon.poseplugin.api.utility.DataTable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//TODO optimize
public abstract class Doppelganger<P, I>
{
    @Getter private final P original;
    @Getter private final int entityId;
    @Getter private final UUID uuid;

    @Getter private Pose pose;
    @Getter private final DataTable<Slot, I> inventory = new DataTable<>();

    protected Doppelganger(P original) {
        this.original = original;
        this.entityId = ThreadLocalRandom.current().nextInt();
        this.uuid = UUID.randomUUID();
    }

    public abstract void spawn();

    public abstract void despawn();

    public abstract void render(P receiver);

    public abstract void unrender(P receiver);

    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();

    public final double distance(double x, double y, double z) {
        return Math.sqrt((x - getX()) * (x - getX()) + (y - getY()) * (y - getY()) + (z - getZ()) * (z - getZ()));
    }





    public final synchronized void setPose(Pose pose) {
        handlePoseChange(this.pose, pose);
        this.pose = pose;
    }

    protected abstract void handlePoseChange(Pose old, Pose newPose);
}
