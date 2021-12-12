package ru.armagidon.poseplugin.api.utility.enums;

public enum Direction
{
    SOUTH,
    WEST,
    NORTH,
    EAST;

    public static Direction yawToDirection(float yaw) {
        return values()[Math.round(yaw / 90) & 0x3];
    }

    public Direction getOpposite() {
        return Direction.values()[(this.ordinal() % 2 == 0 ? 0 : 1) | (isKthBitSet(this.ordinal(), 1) ? 0 : 2)];
    }

    private static boolean isKthBitSet(int in, int k) {
        return (in & (1 << k)) != 0;
    }
}
