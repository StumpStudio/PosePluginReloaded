package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.PosePluginAPI;
import ru.armagidon.poseplugin.api.utility.property.PropertyMap;

public class SitPose<P> extends AbstractPose<P>
{

    @Override
    public void start(P player) {
        PosePluginAPI.<P>getAPI().ifPresent(api ->
                api.getArmorStandSeat().seatPlayer(player));
    }

    @Override
    public void stop(P player) {
        PosePluginAPI.<P>getAPI().ifPresent(api ->
                api.getArmorStandSeat().raisePlayer(player));
    }

    @Override
    protected void registerProperties(PropertyMap propertyMap) {
        propertyMap.register();
    }

    @Override
    public PoseType getPoseType() {
        return PoseType.SITTING;
    }
}
