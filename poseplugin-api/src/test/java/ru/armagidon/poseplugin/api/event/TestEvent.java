package ru.armagidon.poseplugin.api.event;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TestEvent extends Event
{
    private final Map<Object, Object> eventdata = new HashMap<>();

    public Object get(Object key) {
        return eventdata.get(key);
    }

    public void set(Object key, Object value) {
        eventdata.put(key, value);
    }
}
