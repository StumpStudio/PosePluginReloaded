package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import lombok.Getter;
import ru.armagidon.poseplugin.api.utility.datastructures.DataWatcher;
import ru.armagidon.poseplugin.api.utility.datastructures.Pos;
import ru.armagidon.poseplugin.api.utility.enums.Pose;
import ru.armagidon.poseplugin.api.utility.enums.Slot;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//TODO Implement start and end batches
public abstract class Doppelganger<P, I>
{

    @Getter private final P original;
    @Getter private final int entityId;
    @Getter private final UUID uuid;
    @Getter private final Properties properties;

    @Getter private final DataWatcher<Slot, I> inventory = new DataWatcher<>();
    protected DoppelgangerCommandExecutor<P> commandExecutor;


    protected Doppelganger(P original) {
        this.original = original;
        this.entityId = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        this.uuid = UUID.randomUUID();
        this.properties = new Properties();
    }

    public abstract void broadcastSpawn();

    public abstract void broadcastDespawn();

    public abstract void render(P receiver);

    public abstract void unrender(P receiver);

    public abstract Pos getPosition();

    public final boolean isSpawned() {
        return NPCTracker.<P>getInstance().isTracked(this);
    }

    public synchronized void setExecutor(DoppelgangerCommandExecutor executor) {
        this.commandExecutor = executor;
    }


    public record Rot(float pitch, float yaw/*, float roll ?!*/) {}

    public class Properties {
        protected Pose pose;

        synchronized void setPose(Pose pose) {
            this.pose = pose;
        }

        public synchronized Pose getPose() {
            return pose;
        }
    }
}
