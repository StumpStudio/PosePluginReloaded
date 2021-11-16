package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.utility.batch.BatchBuilder;

import java.util.function.Function;

public abstract class PoseBatchBuilder<P> extends BatchBuilder<P>
{
    public abstract PoseBatchBuilder<P> useSeat(Function<Poser<P>, Boolean> dismountCallback);

    public abstract PoseBatchBuilder<P> usePlayerHider();

    public abstract PoseBatchBuilder<P> useDoppelganger();

    public abstract PoseBatchBuilder<P> removeSeat();

    public abstract PoseBatchBuilder<P> turnOffPlayerHiding();

    public abstract PoseBatchBuilder<P> removeDoppelganger();
}
