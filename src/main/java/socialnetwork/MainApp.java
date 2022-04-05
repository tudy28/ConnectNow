package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.database.*;
import socialnetwork.service.EventService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;

public class MainApp extends Application {
    UserDbRepository userDatabaseRepository;
    FriendshipDbRepository friendshipDatabaseRepository;
    MessageDbRepository messageDatabaseRepository;
    ReplyMessageDbRepository replyMessageDatabaseRepository;
    EventDbRepository eventDatabaseRepository;

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    EventService eventService;

    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        userDatabaseRepository=new UserDbRepository("users","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres", new UtilizatorValidator());
        friendshipDatabaseRepository=new FriendshipDbRepository("friendships","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres", new FriendshipValidator());
        messageDatabaseRepository=new MessageDbRepository("messages","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres",new MessageValidator(), userDatabaseRepository);
        replyMessageDatabaseRepository=new ReplyMessageDbRepository("messages","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres",new ReplyMessageValidator(), userDatabaseRepository,messageDatabaseRepository);
        eventDatabaseRepository=new EventDbRepository("events","jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "postgres",new EventValidator(),userDatabaseRepository);

        userService=new UtilizatorService(userDatabaseRepository, friendshipDatabaseRepository);
        friendshipService=new FriendshipService(friendshipDatabaseRepository,userDatabaseRepository);
        messageService=new MessageService(messageDatabaseRepository,replyMessageDatabaseRepository);
        eventService=new EventService(eventDatabaseRepository);

        initView(primaryStage);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setWidth(800);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/views/loginView.fxml"));
        AnchorPane loginLayout = loginLoader.load();
        loginLayout.setOnMousePressed(event->{
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        loginLayout.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        primaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = loginLoader.getController();
        loginController.setMultiService(userService,friendshipService,messageService,eventService);

    }
}