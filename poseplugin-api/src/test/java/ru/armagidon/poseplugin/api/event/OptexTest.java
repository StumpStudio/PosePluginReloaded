package ru.armagidon.poseplugin.api.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.armagidon.poseplugin.api.utility.Optex.optex;

public class OptexTest
{
    @Test
    public void optexTest() {
        TestEvent event = new TestEvent();
        event.set("test", "test");
        optex(event).map(e -> event.get("test")).ifPresent(f -> {
            assertEquals("test", f);
        }).rollbackMapping().ifPresent(f -> assertEquals(event, f));
    }
}
