package ru.armagidon.poseplugin.api.pose.batch;

import java.util.List;

public class Batch
{
    private final List<Runnable> instructions;

    Batch(List<Runnable> instructions) {
        this.instructions = instructions;
    }

    public void run() {
        instructions.forEach(Runnable::run);
    }
}
