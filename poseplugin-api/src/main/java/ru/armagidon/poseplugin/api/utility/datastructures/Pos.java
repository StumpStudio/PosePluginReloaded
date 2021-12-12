package ru.armagidon.poseplugin.api.utility.datastructures;

public record Pos(String world, double x, double y, double z) {

    public double distance(Pos pos) {
        if (!pos.world.equals(world)) return -1;
        final var Dx = pos.x - x;
        final var Dy = pos.y - y;
        final var Dz = pos.z - z;
        return Math.sqrt(Dx * Dx + Dy * Dy + Dz * Dz);
    }

    public boolean withinARadius(Pos other, double radius) {
        final var distance = distance(other);
        return distance <= radius && distance != -1;
    }

    public Pos setY(double y) {
        return new Pos(world, x, y, z);
    }

}
