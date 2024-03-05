package org.food.ordering.system.domain.event;

public final class EmptyEvent implements DomainEvent<Void> {
    public static final EmptyEvent INSTANCE = new EmptyEvent();
    EmptyEvent() {}

    @Override
    public void fire() {

    }
}
