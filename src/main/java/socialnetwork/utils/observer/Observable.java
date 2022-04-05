package socialnetwork.utils.observer;
import socialnetwork.utils.events.EventObserver;

public interface Observable<E extends EventObserver> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
