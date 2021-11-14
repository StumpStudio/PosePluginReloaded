package ru.armagidon.poseplugin.api.subsystems;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Seat<P>
{

    private P seated = null;
    private boolean locked = false;

    public abstract void handlePlayerSeat(@NotNull P player, int yOffset);

    public synchronized void seatPlayer(@NotNull P seated, int yOffset) {
        if (!locked) {
            this.seated = seated;
            handlePlayerSeat(seated, yOffset);
            locked = true;
        }
    }

    public final void seatPlayer(P seated) {
        seatPlayer(seated, 0);
    }

    public synchronized void raisePlayer() {
        if (!locked) return;
        handlePlayerRaise(seated);
        this.seated = null;
    }

    protected abstract void handlePlayerRaise(P seated);

    @Nullable
    public P getSeated() {
        return seated;
    }

    public boolean isUsed() {
        return locked;
    }

    public abstract int getId();
}
