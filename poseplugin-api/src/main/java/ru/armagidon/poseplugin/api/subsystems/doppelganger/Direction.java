package ru.armagidon.poseplugin.api.subsystems.doppelganger;

public enum Direction
{
    SOUTH(),
    WEST(),
    NORTH(),
    EAST();

    public static Direction yawToDirection(float yaw) {
        return values()[Math.round(yaw / 90) & 0x3];
    }
}
