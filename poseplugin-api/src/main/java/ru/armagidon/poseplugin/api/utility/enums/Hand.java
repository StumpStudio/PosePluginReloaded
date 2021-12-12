package ru.armagidon.poseplugin.api.utility.enums;

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
