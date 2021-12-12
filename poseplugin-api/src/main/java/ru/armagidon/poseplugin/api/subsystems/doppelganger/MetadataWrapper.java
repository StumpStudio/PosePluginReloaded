package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import ru.armagidon.poseplugin.api.utility.datastructures.Pos;
import ru.armagidon.poseplugin.api.utility.enums.Hand;
import ru.armagidon.poseplugin.api.utility.enums.Pose;

public abstract class MetadataWrapper<D>
{

    protected final D storage;

    public MetadataWrapper(D storage) {
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
