package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.EventValidationException;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.repository.database.EventDbRepository;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.EventChangeEvent;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventService implements Observable<EventChangeEvent> {
    private EventDbRepository repoEvent;

    public EventService(EventDbRepository repoEvent){
        this.repoEvent=repoEvent;
    }

    public void createEvent(Event event){
        String errors="";
        try{
            repoEvent.save(event);
            notifyObservers(new EventChangeEvent(ChangeEventType.ADD, event));
        }
        catch (EventValidationException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0){
            throw new ServiceException(errors);
        }
    }

    public void removeEvent(Long eventID){
        repoEvent.delete(eventID);
    }

    public void followEvent(Long userID,Long eventID){
        repoEvent.followEvent(userID,eventID);
        notifyObservers(new EventChangeEvent(ChangeEventType.UPDATE_TYPE_1, repoEvent.findOne(eventID).get()));
    }

    public void unfollowEvent(Long userID,Long eventID){
        repoEvent.unfollowEvent(userID,eventID);
        notifyObservers(new EventChangeEvent(ChangeEventType.UPDATE_TYPE_2, repoEvent.findOne(eventID).get()));
    }

    public List<Event> getAllFollowedEvents(Long userID){
        return StreamSupport.stream(repoEvent.getAllFollowedEvents(userID).spliterator(),false)
                .collect(Collectors.toList());
    }

    public List<Utilizator> getAllParticipantsFromEvent(Long eventID){
        return StreamSupport.stream(repoEvent.getAllParticipantsFromEvent(eventID).spliterator(),false)
                .collect(Collectors.toList());
    }


    private List<Observer<EventChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<EventChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EventChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EventChangeEvent t) {
        observers.forEach(x->x.update(t));
    }

    private int pageUnfollowed=-1;
    private int sizeUnfollowed=1;

    public void setPageUnfollowed(int newSizeUnfollowed){
        sizeUnfollowed=newSizeUnfollowed;
    }

    public int getPageUnfollowed(){
        return pageUnfollowed;
    }

    public List<Event> getNextUnfollowed(long userID,long lastID){
        pageUnfollowed++;
        return getUnfollowedOnPage(pageUnfollowed,userID,lastID);

    }

    public List<Event> getUnfollowedOnPage(int page,long userID, long lastID){
        pageUnfollowed=page;
        Pageable pageable=new PageableImplementation(pageUnfollowed,sizeUnfollowed);
        return repoEvent.findAllUnfollowedEventsPaged(pageable,userID,lastID).getContent()
                .collect(Collectors.toList());
    }


    private int pageFollowed=-1;
    private int sizeFollowed=1;

    public void setPageFollowed(int newSizeFollowed){
        sizeFollowed=newSizeFollowed;
    }

    public int getPageFollowed(){
        return pageFollowed;
    }

    public List<Event> getNextFollowed(long userID,long lastID){
        pageFollowed++;
        return getFollowedOnPage(pageFollowed,userID,lastID);

    }

    public List<Event> getFollowedOnPage(int page,long userID, long lastID){
        pageFollowed=page;
        Pageable pageable=new PageableImplementation(pageFollowed,sizeFollowed);
        return repoEvent.findAllFollowedEventsPaged(pageable,userID,lastID).getContent()
                .collect(Collectors.toList());
    }
}
