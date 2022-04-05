package socialnetwork.utils.observer;

import socialnetwork.utils.events.EventObserver;

public interface Observer<E extends EventObserver> {
    void update(E e);
}