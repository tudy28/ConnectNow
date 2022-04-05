package socialnetwork.utils.events;

import socialnetwork.domain.Event;

public class EventChangeEvent implements EventObserver {
    private ChangeEventType type;
    private Event data, oldData;

    public EventChangeEvent(ChangeEventType type, Event data) {
        this.type = type;
        this.data = data;
    }
    public EventChangeEvent(ChangeEventType type, Event data, Event oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Event getData() {
        return data;
    }

    public Event getOldData() {
        return oldData;
    }
}
