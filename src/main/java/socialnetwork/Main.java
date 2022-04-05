package socialnetwork;


import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.ReplyMessageValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.ReplyMessageDbRepository;
import socialnetwork.repository.database.UserDbRepository;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.UI;

import java.util.ArrayList;

//String fileNameUsers = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
public class Main {


    public static void main(String[] args) {

        UserDbRepository userDatabaseRepository = new UserDbRepository("users","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres", new UtilizatorValidator());
        FriendshipDbRepository friendshipDatabaseRepository=new FriendshipDbRepository("friendships","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres", new FriendshipValidator());
        MessageDbRepository messageDatabaseRepository=new MessageDbRepository("messages","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres",new MessageValidator(), userDatabaseRepository);
        ReplyMessageDbRepository replyMessageDatabaseRepository=new ReplyMessageDbRepository("messages","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres",new ReplyMessageValidator(), userDatabaseRepository,messageDatabaseRepository);
        UtilizatorService userService = new UtilizatorService(userDatabaseRepository, friendshipDatabaseRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipDatabaseRepository, userDatabaseRepository);
        MessageService messageService=new MessageService(messageDatabaseRepository,replyMessageDatabaseRepository);
        UI userInterface = new UI(userService, friendshipService,messageService);
        //userInterface.run();
        MainApp.main(args);





    }
}

