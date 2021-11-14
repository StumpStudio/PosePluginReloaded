package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.utility.property.PropertyMap;

public interface Pose<P>
{

    Pose<?> NONE = null;

    void start(P player);

    void stop(P player);

    PropertyMap getProperties();

    P getCurrentUser();
}
