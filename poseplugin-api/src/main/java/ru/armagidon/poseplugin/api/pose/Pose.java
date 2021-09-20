package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.utility.property.PropertyMap;

public interface Pose<P>
{

    Pose<?> STANDING = new StandingPose();

    void start(P player);

    void stop(P player);

    PoseType getPoseType();

    PropertyMap getProperties();

    class StandingPose implements Pose<Object> {

        private final PropertyMap properties = new PropertyMap();

        private StandingPose() {
            properties.register();
        }

        @Override
        public void start(Object player) {}

        @Override
        public void stop(Object player) {}

        @Override
        public PropertyMap getProperties() {
            return properties;
        }

        @Override
        public PoseType getPoseType() {
            return PoseType.STANDING;
        }
    }
}
