package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.exceptions.UserValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.ReplyMessageDbRepository;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.MessageChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MessageService implements Observable<MessageChangeEvent> {
    private MessageDbRepository repoMessage;
    private ReplyMessageDbRepository repoReplyMessage;



    public MessageService(MessageDbRepository repoMessage, ReplyMessageDbRepository repoReplyMessage){
        this.repoMessage=repoMessage;
        this.repoReplyMessage=repoReplyMessage;
    }

    public void sendMessage(Message message){
        String errors="";
        try {
            repoMessage.save(message);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, (ReplyMessage) message));
        }
        catch (IllegalArgumentException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);

    }

    public void replyMessage(ReplyMessage replyMessage){
        String errors="";
        try {
            repoReplyMessage.save(replyMessage);
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, replyMessage));
        }
        catch (IllegalArgumentException e){
            errors+=e.getMessage();
        }
        if(errors.length()>0)
            throw new ServiceException(errors);

    }

    public Iterable<ReplyMessage> getConversation(Long userID_A, Long userID_B){
        return repoReplyMessage.gettConvo(userID_A,userID_B);
    }

    public List<ReplyMessage> getConversationBetweenDates(Long userID_A, Long userID_B, LocalDate date1, LocalDate date2){
        return repoReplyMessage.gettConvoBetweenDates(userID_A,userID_B,date1,date2);
    }

    public Optional<Message> getOneMessage(long id){
        return repoMessage.findOne(id);
    }

    public ReplyMessage getLastMessage(long id1,long id2){
        return repoReplyMessage.getLastMessage(id1,id2);
    }

    private List<Observer<MessageChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x->x.update(t));
    }

    private int pageMessages=-1;
    private int sizeMessages=1;

    public void setPageMessages(int newSizeMessages){
        sizeMessages=newSizeMessages;
    }

    public List<ReplyMessage> getNextMessages(long userA,long userB,long lastID){
        pageMessages++;
        return getMessages(pageMessages,userA,userB,lastID);

    }

    public List<ReplyMessage> getMessages(int page,long userA,long userB,long lastID){
        pageMessages=page;
        Pageable pageable=new PageableImplementation(pageMessages,sizeMessages);
        return repoReplyMessage.findAllConversationPaged(pageable,userA,userB,lastID).getContent()
                .collect(Collectors.toList());
    }

}
