package socialnetwork.domain;

import socialnetwork.service.EventService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.EventChangeEvent;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.MessageChangeEvent;
import socialnetwork.utils.observer.Observer;

import javax.management.Notification;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UserPage {

    private UtilizatorService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private EventService eventService;
    private Utilizator user;
    public UserPage(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService,EventService eventService,Utilizator user){
        this.userService=userService;
        this.friendshipService=friendshipService;
        this.messageService=messageService;
        this.eventService=eventService;
        this.user=user;

    }

    //USEFUL INFO DATA GETTERS///////////////////////////////////////////////////////////////

    public Utilizator getUser(){
        return user;
    }

    public Optional<Utilizator> findUser(long userID){
        return userService.getUser(userID);
    }

    public List<UtilizatorPrietenieDTO> getUserFriendsPaged(long lastID){
        return userService.getNextFriends(user.getId(),lastID);
    }

    public List<UtilizatorPrietenieDTO> getUserFriends(){
        return userService.getFriends(user.getId());
    }

    public List<UtilizatorPrietenieDTO> getUserFriendBetweenDates(LocalDate startDate,LocalDate endDate){
        return userService.getFriendsBetween(user.getId(),startDate,endDate);
    }

    public List<Utilizator> getUserNonFriendsPaged(String filterString,long lastID){
        return userService.getNextNonFriendsFiltered(user.getId(),filterString,lastID);
    }

    public List<Utilizator> getReceivedRequestsPaged(long lastID){
        return userService.getNextUsersRequestReceived(user.getId(),lastID);
    }

    public List<Utilizator> getSentRequestsPaged(long lastID){
        return userService.getNextUsersRequestSent(user.getId(),lastID);
    }

    public ReplyMessage getLastMessageConversation(long otherID){
        return messageService.getLastMessage(user.getId(),otherID);
    }

    public List<ReplyMessage> getConversationBetweenDates(long otherID,LocalDate startDate,LocalDate endDate){
        return messageService.getConversationBetweenDates(user.getId(),otherID,startDate,endDate);
    }

    public List<ReplyMessage> getConversationPaged(long otherID,long lastID){
        return messageService.getNextMessages(user.getId(),otherID,lastID);
    }

    public List<Event> getUnfollowedEventsPaged(long lastID){
        return eventService.getNextUnfollowed(user.getId(),lastID);
    }

    public List<Event> getFollowedEventsPaged(long lastID){
        return eventService.getNextFollowed(user.getId(),lastID);
    }

    public List<Event> getFollowedEvents(){
        return eventService.getAllFollowedEvents(user.getId());
    }

    public List<Utilizator> getEventParticipants(long eventID){
        return eventService.getAllParticipantsFromEvent(eventID);
    }
    //////////////////////////////////////////////////////////////////////////////////////////


    //USEFUL PAGE SIZE GETTERS & SETTERS//////////////////////////////////////////////////////

    //USER FRIENDS
    public void setPageFriendsSize(int newSizeFriends){
        userService.setPageFriendsSize(newSizeFriends);
    }
    public int getPageFriendsSize() {
        return userService.getPageFriendsSize();
    }

    //USER NON FRIENDS
    public void setPageNonFriendsSize(int newSizeNonFriends){
        userService.setPageNonFriendsSize(newSizeNonFriends);
    }
    public int getPageNonFriendsSize(){
        return userService.getPageNonFriendsSize();
    }

    //USER RECEIVED REQUESTS
    public void setPageUsersRequestReceived(int newSizeUsersRequestReceived){
        userService.setPageUsersRequestReceived(newSizeUsersRequestReceived);
    }
    public int getPageUsersRequestReceivedSize(){
        return userService.getPageUsersRequestReceivedSize();
    }

    //USER SENT REQUESTS
    public void setPageUsersRequestSent(int newSizeUsersRequestSent){
        userService.setPageUsersRequestSent(newSizeUsersRequestSent);
    }
    public int getPageUsersRequestSentSize(){
        return userService.getPageUsersRequestSentSize();
    }

    //USER CONVERSATION
    public void setPageMessages(int newSizeMessages){
        messageService.setPageMessages(newSizeMessages);
    }

    //UNFOLLOWED EVENTS
    public void setPageUnfollowed(int newSizeUnfollowed){
        eventService.setPageUnfollowed(newSizeUnfollowed);
    }

    public int getPageUnfollowed(){
        return eventService.getPageUnfollowed();
    }

    //FOLLOWED EVENTS
    public void setPageFollowed(int newSizeFollowed){
        eventService.setPageFollowed(newSizeFollowed);
    }

    public int getPageFollowed(){
        return eventService.getPageFollowed();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////


    //USEFUL FRIENDSHIP WRITERS METHODS///////////////////////////////////////////////////////////
    public void addFriend(long otherID){
        friendshipService.addFriend(user.getId(),otherID);
    }

    public void removeFriend(long otherID){
        friendshipService.removeFriend(user.getId(),otherID);
    }

    public void acceptRequest(long otherID){
        friendshipService.acceptRequest(otherID,user.getId());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


    //USEFUL MESSAGE WRITERS METHODS//////////////////////////////////////////////////////////////
    public void sendMessage(Message message){
        messageService.sendMessage(message);
    }

    public void replyMessage(ReplyMessage replyMessage){
        messageService.replyMessage(replyMessage);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////


    //USEFUL EVENT WRITERS METHODS////////////////////////////////////////////////////////////////
    public void createEvent(Event event){
        eventService.createEvent(event);
    }

    public void removeEvent(Long eventID){
        eventService.removeEvent(eventID);
    }

    public void followEvent(Long eventID){
        eventService.followEvent(user.getId(),eventID);
    }

    public void unfollowEvent(Long eventID){
        eventService.unfollowEvent(user.getId(),eventID);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    //OBSERVERS METHODS////////////////////////////////////////////////////////////////////////////
    public void addFriendshipObserver(Observer<FriendshipChangeEvent> friendshipEvent) {
        friendshipService.addObserver(friendshipEvent);
    }

    public void addMessageObserver(Observer<MessageChangeEvent> messageEvent){
        messageService.addObserver(messageEvent);
    }

    public void addEventObserver(Observer<EventChangeEvent> eventEvent){
        eventService.addObserver(eventEvent);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////



}
