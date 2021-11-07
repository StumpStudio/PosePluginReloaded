package ru.armagidon.poseplugin.api;

import ru.armagidon.poseplugin.api.utility.Pool;
import ru.armagidon.poseplugin.api.utility.Seat;

public abstract class PosePluginAPI<P>
{
    private static PosePluginAPI<?> API;

    public static <P> void init(PosePluginAPI<P> apiInstance) {
        if (API != null)
            throw new IllegalStateException("API is already initialized");
        API = apiInstance;
    }

    protected PosePluginAPI() {
        if (API != null)
            throw new IllegalStateException("API is already initialized");
    }

    @SuppressWarnings("unchecked")
    public static <P> PosePluginAPI<P> getAPI() {
        return (PosePluginAPI<P>) API;
    }

    public abstract Pool<Seat<P>> getSeats();
}
