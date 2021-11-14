package ru.armagidon.poseplugin.api.subsystems.doppelganger;

public enum Hand
{
    MAIN(true), OFF(false);

    private final boolean flag;

    Hand(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }
}
