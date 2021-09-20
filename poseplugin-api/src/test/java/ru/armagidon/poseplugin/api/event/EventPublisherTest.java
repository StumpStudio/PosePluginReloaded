package ru.armagidon.poseplugin.api.event;

import org.junit.Assert;
import org.junit.Test;

public class EventPublisherTest {

    @Test
    public void testSubscriberSorting() {
        EventSubscriber<TestEvent> subscriber1 = new EventSubscriber<>() {
            @Override
            public void call(TestEvent event) {
                System.out.println("SUBSCRIBER 1: NORMAL PRIORITY");
                event.set("priority", priority());
            }
        };

        EventSubscriber<TestEvent> subscriber2 = new EventSubscriber<>() {
            @Override
            public void call(TestEvent event) {
                System.out.println("SUBSCRIBER 2: HIGHEST PRIORITY");
                event.set("priority", priority());
            }

            @Override
            public Priority priority() {
                return Priority.HIGHEST;
            }
        };

        EventSubscriber<TestEvent> subscriber3 = new EventSubscriber<>() {
            @Override
            public void call(TestEvent event) {
                System.out.println("SUBSCRIBER 3: LOWEST PRIORITY");
                event.set("priority", priority());
            }

            @Override
            public Priority priority() {
                return Priority.LOWEST;
            }
        };



        EventPublisher.subscribe(TestEvent.class, subscriber1);
        EventPublisher.subscribe(TestEvent.class, subscriber2);
        EventPublisher.subscribe(TestEvent.class, subscriber3);

        TestEvent event = EventPublisher.call(new TestEvent());

        Assert.assertEquals(Priority.HIGHEST, event.get("priority"));

        EventPublisher.unsubscribeAll(TestEvent.class);

    }

    @Test
    public void testMultiSubscriberEventHandling() {
        class TestSubscriber implements MultiSubscriber {
            @Subscribe
            public void test(TestEvent event) {
                System.out.println("TEST EVENT");
                event.set("handled", true);
            }

            @Subscribe(priority = Priority.LOWEST)
            public void test2(TestEvent event) {
                System.out.println("PRIORITY LOWEST");
                event.set("lowest-handled", true);
            }
        }

        EventPublisher.subscribe(new TestSubscriber());

        TestEvent event = EventPublisher.call(new TestEvent());

        Assert.assertNotNull(event.get("handled"));
        Assert.assertNotNull(event.get("lowest-handled"));

    }
}