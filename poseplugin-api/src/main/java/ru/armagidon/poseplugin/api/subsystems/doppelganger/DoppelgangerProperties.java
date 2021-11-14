package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger.Pos;

public abstract class DoppelgangerProperties<P, D>
{

    protected final P source;
    protected final D storage;

    public DoppelgangerProperties(P source, D storage) {
        this.source = source;
        this.storage = storage;
    }

    public abstract void setPose(Pose pose);

    public abstract void setBedPosition(Pos pos);

    public abstract void setInvisible(boolean invisibility);

    public abstract void setOverlays(byte overlays);

    public abstract void setActiveHand(Hand hand);

    public abstract void activateHand();

    public abstract void deactivateHand();

    public abstract void setRiptide(boolean riptideState);

    public abstract Pose getPose();

    public abstract Pos getBedPosition();

    public abstract boolean isInvisible();

    public abstract byte getOverlays();

    public abstract Hand getActiveHand();

    public abstract boolean isHandActivated();

    public abstract boolean isRiptiding();

    public D getStorage() {
        return storage;
    }
}
