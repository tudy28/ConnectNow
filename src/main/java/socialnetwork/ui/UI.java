package socialnetwork.ui;

import jdk.jshell.execution.Util;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.util.ArrayList;
import java.util.Scanner;

public class UI {
    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    private long lastID;

    /**
     * Metoda care construieste un obiect de tipul UI
     * @param userService un obiect de tipul utilizatorService
     * @param friendshipService un obiect de tipul friendshipService
     */
    public UI(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService){
        this.userService=userService;
        this.friendshipService=friendshipService;
        this.messageService=messageService;
        lastID=userService.getLastID()+1;
    }


    public void displayMenu(){
        System.out.println("1.Add a user");
        System.out.println("2.Remove a user");
        System.out.println("3.Add a friend");
        System.out.println("4.Remove a friend");
        System.out.println("5.Display the number of communities");
        System.out.println("6.Display the most sociable community");
        System.out.println("7.Display all the users");
        System.out.println("8.Become a user");
        System.out.println("9.Exit");
    }

    public void addUser(){
        Scanner scanner=new Scanner(System.in);
        String first_name;
        String last_name;
        String email;
        String password;
        System.out.print("Enter the first name: ");
        first_name=scanner.nextLine();
        System.out.print("Enter the last name: ");
        last_name=scanner.nextLine();
        System.out.print("Enter the email: ");
        email=scanner.nextLine();
        System.out.print("Enter the password: ");
        password=scanner.nextLine();

        Utilizator user=new Utilizator(first_name,last_name,email,password);
        user.setId(this.lastID);
        try {
            userService.addUtilizator(user);
            this.lastID = this.lastID + 1;
            System.out.println("User added...");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    public void removeUser(){
        try {
            Scanner scanner =new Scanner(System.in);
            displayUsers();
            System.out.print("Choose a user ID to be removed: ");
            String idStr = scanner.nextLine();
            long id=Long.parseLong(idStr);
            userService.removeUtilizator(id);
            System.out.println("User removed...");
        }
        catch (IllegalArgumentException e){
            System.out.println("ID must be a number...");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }

    }

    public void addFriend(){
        try {
            Scanner scanner = new Scanner(System.in);
            displayUsers();
            System.out.print("Choose the first ID: ");
            String id1Str = scanner.nextLine();
            System.out.print("Choose the second ID: ");
            String id2Str = scanner.nextLine();
            long id1=Long.parseLong(id1Str);
            long id2=Long.parseLong(id2Str);
            friendshipService.addFriend(id1, id2);
            System.out.println("The 2 users are now friends...");
        }
        catch (IllegalArgumentException e){
            System.out.println("Both IDs must be numbers...");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    public void removeFriend(){
        try {
            Scanner scanner = new Scanner(System.in);
            displayUsers();
            System.out.print("Choose the first ID: ");
            String id1Str = scanner.nextLine();
            System.out.print("Choose the second ID: ");
            String id2Str = scanner.nextLine();
            long id1=Long.parseLong(id1Str);
            long id2=Long.parseLong(id2Str);
            friendshipService.removeFriend(id1, id2);
            System.out.println("The 2 users are not friends anymore...");

        }
        catch (IllegalArgumentException e){
            System.out.println("Both IDs must be numbers...");
        }
        catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    public void numberOfCommunities(){
        System.out.println("The number of communities is: "+ friendshipService.numberOfCommunities());
    }

    public void mostSociable(){

        ArrayList<Long> users=friendshipService.mostSocial();
        System.out.print("The most sociable community contains the following IDs: ");
        for(int i=0;i<users.size()-1;i++)
            System.out.print(users.get(i)+" ");
        System.out.println();
        System.out.println("The 'length' of the maximum path of this community is: "+users.get(users.size()-1));
        System.out.println();
    }

    public void becomeUser(){
        try {
            Scanner scanner =new Scanner(System.in);
            for(Utilizator u:userService.getAll())
                System.out.println("ID="+u.getId()+" "+u.getFirstName()+" "+u.getLastName());
            System.out.print("Enter the ID of the user you want to become: ");
            String idStr = scanner.nextLine();
            long id=Long.parseLong(idStr);
            if(userService.getUser(id).isPresent()){
                UserUI userUI=new UserUI(userService,friendshipService,messageService,id);
                userUI.run();
            }
            else{
                System.out.println("User doesn't exist...");
            }
        }
        catch (IllegalArgumentException e){
            System.out.println("ID must be a number...");
        }

    }

    public void updateUser(){
    }

    public void displayUsers(){
        userService.getAll().forEach(System.out::println);
    }

    public void run(){
        int choice;
        boolean go=true;
        while(go){
            Scanner scanner= new Scanner(System.in);
            displayMenu();
            System.out.print("Choose a command: ");
            choice=scanner.nextInt();
            switch (choice){
                case 1:
                    addUser();
                    break;
                case 2:
                    removeUser();
                    break;
                case 3:
                    addFriend();
                    break;
                case 4:
                    removeFriend();
                    break;
                case 5:
                    numberOfCommunities();
                    break;
                case 6:
                    mostSociable();
                    break;
                case 7:
                    displayUsers();
                    break;
                case 8:
                    becomeUser();
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
