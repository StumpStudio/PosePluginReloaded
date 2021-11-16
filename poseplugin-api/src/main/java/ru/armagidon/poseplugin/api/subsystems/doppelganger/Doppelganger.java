package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import lombok.Getter;
import lombok.Setter;
import ru.armagidon.poseplugin.api.utility.DataTable;
import ru.armagidon.poseplugin.api.utility.batch.Batch;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//TODO Implement start and end batches
public abstract class Doppelganger<P, I, D>
{

    @Getter private final P original;
    @Getter private final int entityId;
    @Getter private final UUID uuid;

    @Getter private final DataTable<Slot, I> inventory = new DataTable<>();
    @Getter protected DoppelgangerProperties<P, D> properties;

    @Setter protected Batch<P> startBatch;
    @Setter protected Batch<P> endBatch;

    protected Doppelganger(P original) {
        this.original = original;
        this.entityId = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        this.uuid = UUID.randomUUID();
    }

    public abstract void broadcastSpawn();

    public abstract void broadcastDespawn();

    public abstract void render(P receiver);

    public abstract void unrender(P receiver);

    //public abstract void lay(Direction direction);

    public abstract Pos getPosition();

    public final boolean isSpawned() {
        return NPCTracker.<P>getInstance().isTracked(this);
    }

    public record Pos(String world, double x, double y, double z) {

        public double distance(Pos pos) {
            if (!pos.world.equals(world)) return -1;
            final var Dx = pos.x - x;
            final var Dy = pos.y - y;
            final var Dz = pos.z - z;
            return Math.sqrt(Dx * Dx + Dy * Dy + Dz * Dz);
        }

        public boolean withinARadius(Pos other, double radius) {
            final var distance = distance(other);
            return distance <= radius && distance != -1;
        }

        public Pos setY(double y) {
            return new Pos(world, x, y, z);
        }

    }

    public record Rot(float pitch, float yaw/*, float roll ?!*/) {}
}
