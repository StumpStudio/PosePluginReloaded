package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.utility.property.PropertyMap;

public abstract class AbstractPose<P> implements IPluginPose<P>
{

    private final PropertyMap propertyMap = new PropertyMap();

    public AbstractPose() {
        registerProperties(propertyMap);
    }

    @Override
    public final PropertyMap getProperties() {
        return propertyMap;
    }

    protected abstract void registerProperties(PropertyMap propertyMap);
}
