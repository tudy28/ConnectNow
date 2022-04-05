package socialnetwork.ui;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.UtilizatorPrietenieDTO;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.Constants;
import java.text.DateFormatSymbols;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class UserUI {
    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    private long userID;

    public UserUI(UtilizatorService userService,FriendshipService friendshipService,MessageService messageService,long userID){
        this.userService=userService;
        this.friendshipService=friendshipService;
        this.messageService=messageService;
        this.userID=userID;
    }

    public void displayUsers(){
        for(Utilizator u:userService.getAll())
            if(u.getId()!=userID)
                System.out.println(u);
    }

    public void displayMenu(){
        System.out.println("1.Add a friend");
        System.out.println("2.Remove a friend");
        System.out.println("3.Display all the friends");
        System.out.println("4.Display all the friends from a month");
        System.out.println("5.Accept friend requests");
        System.out.println("6.Send a message");
        System.out.println("7.Reply to a message");
        System.out.println("8.Display conversations");
        System.out.println("9.Back to main menu");
    }

    public void addFriend(){
        try {
            Scanner scanner = new Scanner(System.in);
            displayUsers();
            System.out.print("Enter the ID of the user you want to send a friend request: ");
            String id2Str = scanner.nextLine();
            long id2=Long.parseLong(id2Str);
            friendshipService.addFriend(userID, id2);
            System.out.println("The friend request was sent successfully...");
        }
        catch (IllegalArgumentException e){
            System.out.println("The ID must be a number...");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }


    public void displayFriends(){
        for(UtilizatorPrietenieDTO dto:userService.getFriends(userID))
            System.out.println(dto);
    }



    public void displayFriendsMonth(){
        Month month;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the month(as a number between 1 and 12): ");
        try {
            int monthNumber=scanner.nextInt();
            month = Month.of(monthNumber);
            for(UtilizatorPrietenieDTO dto:userService.getFriendsMonth(userID,month))
                System.out.println(dto);
        }
        catch ( Exception e){
            System.out.println("Please enter a number between 1 and 12...");
        }
    }

    public void acceptFriendRequests(){
        System.out.println("You have friend requests from:");
        for(Utilizator u:userService.getReceivedFriendRequests(userID))
            System.out.println("ID="+u.getId()+" "+u.getFirstName()+" "+u.getLastName());
        System.out.println("Enter a user ID to accept his friend request: ");
        Scanner scanner = new Scanner(System.in);
        try{
            long idRequester=scanner.nextLong();
            friendshipService.acceptRequest(idRequester,userID);
            System.out.println("You are now friend with "+userService.getUser(idRequester).get().getLastName()+"...");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(){
        Scanner scanner = new Scanner(System.in);
        displayUsers();
        System.out.println("Enter the IDs of the users you want to send a message");
        long id = 0;
        Set<Long> id_set=new HashSet<Long>();
        while(id!=-1){
            System.out.print("Enter a ID(or -1 if you want to stop): ");
            try {
                id = scanner.nextLong();
                if (id != -1)
                    id_set.add(id);
            }
            catch (Exception e){
                System.out.println("Please enter only numbers...");
                scanner.nextLine();
            }
        }
        String message;
        System.out.print("Enter the message you want to send: ");
        scanner.nextLine();
        message=scanner.nextLine();
        ArrayList<Utilizator> to_users=new ArrayList<Utilizator>();
        for(Long to_id: id_set)
            userService.getUser(to_id).ifPresent(to_users::add);
        if(userService.getUser(userID).isPresent()) {
            Message messageToSend = new Message(userService.getUser(userID).get(), to_users, message);
            try {
                messageService.sendMessage(messageToSend);
                System.out.println("The message was sent...");
            }
            catch (ServiceException e){
                System.out.println(e.getMessage());
            }
        }


    }

    public void replyMessage(){
        Scanner scanner = new Scanner(System.in);
        displayUsers();
        System.out.print("Enter the ID of the user you want to reply at a message: ");
        long id = 0;
        try {
            id = scanner.nextLong();
            boolean convoExists=false;
            for(Message m:messageService.getConversation(userID,id)) {
                System.out.println("ID:"+m.getId()+" "+m);
                convoExists=true;
            }
            if(convoExists){
                long id_message=0;
                String reply_message;
                System.out.print("Enter the ID of the message you want to reply: ");
                id_message=scanner.nextLong();
                ArrayList<Utilizator> to_users=new ArrayList<Utilizator>();
                userService.getUser(id).ifPresent(to_users::add);
                scanner.nextLine();
                System.out.print("Enter the reply message: ");
                reply_message=scanner.nextLine();
                if(userService.getUser(userID).isPresent() && messageService.getOneMessage(id_message).isPresent()) {
                    ReplyMessage replyMessageToSend = new ReplyMessage(userService.getUser(userID).get(), to_users, reply_message, messageService.getOneMessage(id_message).get());
                    try {
                        messageService.replyMessage(replyMessageToSend);
                        System.out.println("The reply message was sent...");
                    }
                    catch (ServiceException e){
                        System.out.println(e.getMessage());
                    }
                }

            }
            else System.out.println("You don't have any conversation with this user...");
        }
        catch (Exception e){
            System.out.println("Please enter only numbers...");
            scanner.nextLine();
        }

    }

    public void displayConversation(){
        Scanner scanner = new Scanner(System.in);
        displayUsers();
        System.out.print("Enter the ID of the user you want to see the conversation with: ");
        long id;
        try {
            id = scanner.nextLong();
            System.out.println();
            boolean convoExists=false;
            for(ReplyMessage m:messageService.getConversation(userID,id)) {
                if(m.getRepliedMessage()==null)
                    System.out.println(m);
                else
                    System.out.println("Replied at: "+m.getRepliedMessage().getMessage()+" From: "+m.getRepliedMessage().getFrom().getLastName()+" "+m.getRepliedMessage().getFrom().getFirstName()+"\n"+m);
                convoExists=true;
            }
            if(!convoExists)
                System.out.println("You don't have any conversation with this user...");
        }
        catch (Exception e){
            System.out.println("Please enter only numbers...");
            scanner.nextLine();
        }
    }


    public void run(){
        int choice;
        boolean go=true;
        Optional<Utilizator> user=userService.getUser(userID);
        user.ifPresent(utilizator -> System.out.println("You are logged now as " + utilizator.getFirstName() + " " + utilizator.getLastName() + ", ID:" + utilizator.getId()));
        while(go){
            Scanner scanner= new Scanner(System.in);
            displayMenu();
            System.out.print("Choose a command: ");
            choice=scanner.nextInt();
            switch (choice){
                case 1:
                    addFriend();
                    break;
                case 2:

                    break;
                case 3:
                    displayFriends();
                    break;
                case 4:
                    displayFriendsMonth();
                    break;
                case 5:
                    acceptFriendRequests();
                    break;
                case 6:
                    sendMessage();
                    break;
                case 7:
                    replyMessage();
                    break;
                case 8:
                    displayConversation();
                    break;
                case 9:
                    go=false;
                    break;
                default:
                    System.out.println("Invalid command...");
            }
        }
    }
}
