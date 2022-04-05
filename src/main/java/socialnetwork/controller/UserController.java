package socialnetwork.controller;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import org.postgresql.ds.PGSimpleDataSource;
import socialnetwork.domain.Event;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.UserPage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.UtilizatorPrietenieDTO;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.Constants;
import socialnetwork.utils.PDFCreator.PDFGenerator;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.EventObserver;
import socialnetwork.utils.observer.Observer;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer {
    UserPage userPage;



    ObservableList<UtilizatorPrietenieDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUsers= FXCollections.observableArrayList();
    ObservableList<ReplyMessage> modelMessages=FXCollections.observableArrayList();
    ObservableList<Utilizator> modelReceivedRequests=FXCollections.observableArrayList();
    ObservableList<Utilizator> modelSentRequests=FXCollections.observableArrayList();
    ObservableList<Event> modelUnfollowedEvents=FXCollections.observableArrayList();
    ObservableList<Event> modelFollowedEvents=FXCollections.observableArrayList();

    private UtilizatorPrietenieDTO selectedUserToChat;

    private Stage stage;

    @FXML
    private Button buttonLogout;

    @FXML
    private Button buttonSendMessage;

    @FXML
    private Button buttonDeleteFriend;

    @FXML
    private Button buttonAddFriendAction;

    @FXML
    private Button buttonChangeRequests;

    @FXML
    private Button buttonRequestAccept;

    @FXML
    private Button buttonRequestReject;

    @FXML
    private Button buttonRequestCancel;

    @FXML
    private Button buttonSaveFile;

    @FXML
    private Button buttonFollowEvent;

    @FXML
    private Button buttonUnfollowEvent;

    @FXML
    private Button buttonChangeEvents;



    //-----------------------TABELA USERS----------------------------------------------
    @FXML
    private TableView<Utilizator> tableViewUsers;

    @FXML
    private TableColumn<Utilizator, String> tableUsersColumnEmail;

    @FXML
    private TableColumn<Utilizator, String> tableUsersColumnFirstName;

    @FXML
    private TableColumn<Utilizator, String> tableUsersColumnLastName;
    //-----------------------------------------------------------------------------------

    //-----------------------TABELA RECEIVED REQUESTS----------------------------------------------
    @FXML
    private TableView<Utilizator> tableViewReceivedRequests;

    @FXML
    private TableColumn<Utilizator, String> tableReceivedColumnEmail;

    @FXML
    private TableColumn<Utilizator, String> tableReceivedColumnFirstName;

    @FXML
    private TableColumn<Utilizator, String> tableReceivedColumnLastName;
    //-----------------------------------------------------------------------------------

    //-----------------------TABELA SENT REQUESTS----------------------------------------------
    @FXML
    private TableView<Utilizator> tableViewSentRequests;

    @FXML
    private TableColumn<Utilizator, String> tableSentColumnEmail;

    @FXML
    private TableColumn<Utilizator, String> tableSentColumnFirstName;

    @FXML
    private TableColumn<Utilizator, String> tableSentColumnLastName;
    //-----------------------------------------------------------------------------------

    //-----------------------TABELA FRIENDS----------------------------------------------
    @FXML
    private TableView<UtilizatorPrietenieDTO> tableViewFriends;

    @FXML
    private TableColumn<UtilizatorPrietenieDTO,String> tableFriendsColumnEmail;

    @FXML
    private TableColumn<UtilizatorPrietenieDTO,String> tableFriendsColumnFirstName;

    @FXML
    private TableColumn<UtilizatorPrietenieDTO,String> tableFriendsColumnLastName;
    //------------------------------------------------------------------------------------

    //----------------------TABELA UNFOLLOWED EVENTS--------------------------------------
    @FXML
    private TableView<Event> tableViewUnfollowedEvents;

    @FXML
    private TableColumn<Event,String> tableUnfollowedEventsName;

    @FXML
    private TableColumn<Event, LocalDateTime> tableUnfollowedEventsStart;
    //------------------------------------------------------------------------------------

    //----------------------TABELA FOLLOWED EVENTS--------------------------------------
    @FXML
    private TableView<Event> tableViewFollowedEvents;

    @FXML
    private TableColumn<Event,String> tableFollowedEventsName;

    @FXML
    private TableColumn<Event, LocalDateTime> tableFollowedEventsStart;
    //------------------------------------------------------------------------------------


    @FXML
    private TabPane tabPane;

    @FXML
    private TabPane tabPaneTables;

    @FXML
    private TextField textFieldSearchUser;

    @FXML
    private TextField textFieldTypeMessage;

    @FXML
    private TextField textFieldPDFName;

    @FXML
    private TextField textFieldFileOutput;

    @FXML
    private TextField textFieldEventName;

    @FXML
    private TextArea textAreaEventDescription;


    @FXML
    private ListView<ReplyMessage> listViewChat;

    @FXML
    private ListView<String> listViewReport;

    @FXML
    private ListView<String> listViewEvent;

    @FXML
    private ListView<String> listViewUpcoming;


    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private DatePicker datePickerEventStart;

    @FXML
    private DatePicker datePickerEventEnd;

    @FXML
    private ComboBox<String> comboBoxReport;


    @FXML
    private AnchorPane anchorSaveFile;

    @FXML
    private AnchorPane anchorInfo;

    @FXML
    private AnchorPane anchorAddEvent;

    @FXML
    Spinner<LocalTime> startSpinner;

    @FXML
    Spinner<LocalTime> endSpinner;

    @FXML
    Label nameLabel;

    @FXML
    Label dateLabel;

    Timeline clock;

    private void initClock(Timeline clock){
        clock=new Timeline(new KeyFrame(Duration.ZERO, e -> {
            dateLabel.setText(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_NICE));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void initTimeSpinner(Spinner<LocalTime> spinner){
        SpinnerValueFactory<LocalTime> value = new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(new LocalTimeStringConverter(FormatStyle.SHORT));
                setValue(LocalTime.now());
            }
            @Override
            public void decrement(int steps) {
                    LocalTime time = getValue();
                    setValue(time.minusMinutes(steps));
            }

            @Override
            public void increment(int steps) {
                    LocalTime time =  getValue();
                    setValue(time.plusMinutes(steps));
            }
        };

        spinner.setValueFactory(value);
        spinner.setEditable(false);
    }

    void initTables(){
        tableFriendsColumnEmail.setCellValueFactory(new PropertyValueFactory<UtilizatorPrietenieDTO,String>("email"));
        tableFriendsColumnFirstName.setCellValueFactory(new PropertyValueFactory<UtilizatorPrietenieDTO,String>("firstName"));
        tableFriendsColumnLastName.setCellValueFactory(new PropertyValueFactory<UtilizatorPrietenieDTO,String>("lastName"));

        tableUsersColumnEmail.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("email"));
        tableUsersColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableUsersColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));

        tableReceivedColumnEmail.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("email"));
        tableReceivedColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableReceivedColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));

        tableSentColumnEmail.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("email"));
        tableSentColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableSentColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));

        tableUnfollowedEventsName.setCellValueFactory(new PropertyValueFactory<Event,String>("name"));
        tableUnfollowedEventsStart.setCellValueFactory(new PropertyValueFactory<Event,LocalDateTime>("startDate"));
        tableUnfollowedEventsStart.setCellFactory(column-> new TableCell<Event, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(Constants.DATE_TIME_FORMATTER.format(item));
                }
            }
        });

        tableFollowedEventsName.setCellValueFactory(new PropertyValueFactory<Event,String>("name"));
        tableFollowedEventsStart.setCellValueFactory(new PropertyValueFactory<Event,LocalDateTime>("startDate"));
        tableFollowedEventsStart.setCellFactory(column-> new TableCell<Event, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(Constants.DATE_TIME_FORMATTER.format(item));
                }
            }
        });
    }


    ////////////////SCROLL HANDLERS///////////////////////////////////////////
    EventHandler<ScrollEvent> handlerScrollFriends=event -> {
        if(tableViewFriends.getItems().size()>0) {
            userPage.setPageFriendsSize(2);
            modelFriends.addAll(userPage.getUserFriendsPaged(tableViewFriends.getItems().get(tableViewFriends.getItems().size() - 1).getId()));
            List<UtilizatorPrietenieDTO> friendsList = new ArrayList<>(modelFriends);
            modelFriends.setAll(friendsList);
        }
    };

    EventHandler<ScrollEvent> handlerScrollSearchNonFriends=event-> {
        if(tableViewUsers.getItems().size()>0) {
            userPage.setPageNonFriendsSize(2);
            modelUsers.addAll(userPage.getUserNonFriendsPaged(textFieldSearchUser.getText(), tableViewUsers.getItems().get(tableViewUsers.getItems().size() - 1).getId()));
            List<Utilizator> usersList = new ArrayList<>(modelUsers);
            modelUsers.setAll(usersList);
        }
    };

    EventHandler<ScrollEvent> handlerScrollRequestsReceived=event-> {
        if(tableViewReceivedRequests.getItems().size()>0) {
            userPage.setPageUsersRequestReceived(2);
            modelReceivedRequests.addAll(userPage.getReceivedRequestsPaged(tableViewReceivedRequests.getItems().get(tableViewReceivedRequests.getItems().size() - 1).getId()));
            List<Utilizator> usersList = new ArrayList<>(modelReceivedRequests);
            modelReceivedRequests.setAll(usersList);
        }
    };

    EventHandler<ScrollEvent> handlerScrollRequestsSent=event-> {
        if(tableViewSentRequests.getItems().size()>0) {
            userPage.setPageUsersRequestSent(2);
            modelSentRequests.addAll(userPage.getSentRequestsPaged(tableViewSentRequests.getItems().get(tableViewSentRequests.getItems().size() - 1).getId()));
            List<Utilizator> usersList = new ArrayList<>(modelSentRequests);
            modelSentRequests.setAll(usersList);
        }
    };

    EventHandler<ScrollEvent> handlerScrollMessages=event->{
        if(listViewChat.getItems().size()>0) {
            userPage.setPageMessages(1);
            List<ReplyMessage> messageList = new ArrayList<>(modelMessages);
            messageList.addAll(0, userPage.getConversationPaged(selectedUserToChat.getId(), listViewChat.getItems().get(0).getId()));
            modelMessages.setAll(messageList);
        }
    };

    EventHandler<ScrollEvent> handlerScrollUnfollowed=event->{
        if(tableViewUnfollowedEvents.getItems().size()>0){
            userPage.setPageUnfollowed(2);
            modelUnfollowedEvents.addAll(userPage.getUnfollowedEventsPaged(tableViewUnfollowedEvents.getItems().get(tableViewUnfollowedEvents.getItems().size()-1).getId()));
            List<Event> eventList=new ArrayList<>(modelUnfollowedEvents);
            modelUnfollowedEvents.setAll(eventList);
        }
    };

    EventHandler<ScrollEvent> handlerScrollFollowed=event->{
        if(tableViewFollowedEvents.getItems().size()>0){
            userPage.setPageFollowed(2);
            modelFollowedEvents.addAll(userPage.getFollowedEventsPaged(tableViewFollowedEvents.getItems().get(tableViewFollowedEvents.getItems().size()-1).getId()));
            List<Event> eventList=new ArrayList<>(modelFollowedEvents);
            modelFollowedEvents.setAll(eventList);
        }
    };

    public void initScrollDeploy(){
        deployLoadNonFriendsPage();
        deployLoadFriendsPage();
        deployLoadUsersSentRequests();
        deployLoadUsersReceivedRequests();
        deployLoadMessages();
        deployLoadFollowedEvents();
        deployLoadUnfollowedEvents();
    }

    ////////////////SCROLL HANDLERS///////////////////////////////////////////


    public void setMultiService(UserPage userPage) {
        this.userPage=userPage;
        tabPane.getSelectionModel().select(1);
        tabPaneTables.setVisible(false);

        userPage.addFriendshipObserver(friendshipChangeEvent -> {
            if(friendshipChangeEvent.getType()== ChangeEventType.DELETE) {
                refreshModelFriends(tableViewFriends.getItems().size());
                refreshModelReceivedRequests(tableViewReceivedRequests.getItems().size());
            }
            else if(friendshipChangeEvent.getType()==ChangeEventType.ADD) {
                refreshModelFriends(tableViewFriends.getItems().size() + 1);
                refreshModelReceivedRequests(tableViewReceivedRequests.getItems().size()+1);
            }
            refreshModelNonFriends();
            refreshModelSentRequests();
        });
         userPage.addMessageObserver(messageChangeEvent -> {
             refreshConversation();
         });
         userPage.addEventObserver(eventChangeEvent -> {
             if(eventChangeEvent.getType()==ChangeEventType.ADD) {
                 refreshModelUnfollowedEvents(tableViewUnfollowedEvents.getItems().size() + 1);
             }
             else if(eventChangeEvent.getType()==ChangeEventType.UPDATE_TYPE_1){
                 refreshModelFollowedEvents(tableViewFollowedEvents.getItems().size()+1);
                 refreshModelUnfollowedEvents(tableViewUnfollowedEvents.getItems().size());
             }
             else if(eventChangeEvent.getType()==ChangeEventType.UPDATE_TYPE_2){
                 refreshModelFollowedEvents(tableViewFollowedEvents.getItems().size());
                 refreshModelUnfollowedEvents(tableViewUnfollowedEvents.getItems().size()+1);
             }
         });

        tabPaneTables.setVisible(true);
        tabPaneTables.getSelectionModel().select(0);
        initModelFriends();
        tableViewFriends.setItems(modelFriends);
        AnchorPane.setTopAnchor(tabPaneTables,0.0);
        AnchorPane.setBottomAnchor(tabPaneTables,82.0);

        initTables();
        initScrollDeploy();

        textFieldSearchUser.textProperty().addListener(x->handleFilterUsers());
        initTimeSpinner(startSpinner);
        initTimeSpinner(endSpinner);



        initClock(clock);


        nameLabel.setText("Hello there, "+userPage.getUser().getFirstName()+" "+userPage.getUser().getLastName());
        anchorInfo.setVisible(true);

        sendNotification();

    }


    //------------------------------------------INIT MODELS--------------------------------------------//
    private void initModelFriends() {
        userPage.setPageFriendsSize(20);
        modelFriends.setAll(userPage.getUserFriendsPaged(0));

    }

    private void refreshModelFriends(int numberOfLoadings){
        int lastValue=userPage.getPageFriendsSize();
        userPage.setPageFriendsSize(numberOfLoadings);
        modelFriends.setAll(userPage.getUserFriendsPaged(0));
        userPage.setPageFriendsSize(lastValue);
    }

    private void deployLoadFriendsPage(){
        tableViewFriends.addEventFilter(ScrollEvent.ANY,handlerScrollFriends);
    }


    private void initModelNonFriends() {
        userPage.setPageNonFriendsSize(17);
    }

    private void refreshModelNonFriends(){
        int lastValue=userPage.getPageNonFriendsSize();
        userPage.setPageNonFriendsSize(tableViewUsers.getItems().size());
        modelUsers.setAll(userPage.getUserNonFriendsPaged(textFieldSearchUser.getText(),0));
        userPage.setPageNonFriendsSize(lastValue);
    }

    private void deployLoadNonFriendsPage(){
        tableViewUsers.addEventFilter(ScrollEvent.ANY,handlerScrollSearchNonFriends);
    }



    private void initModelUsersReceivedRequests(){
        userPage.setPageUsersRequestReceived(16);
        modelReceivedRequests.setAll(userPage.getReceivedRequestsPaged(0));
    }

    private void refreshModelReceivedRequests(int numberOfLoadings){
        int lastValue=userPage.getPageUsersRequestReceivedSize();
        userPage.setPageUsersRequestReceived(numberOfLoadings);
        modelReceivedRequests.setAll(userPage.getReceivedRequestsPaged(0));
        userPage.setPageUsersRequestReceived(lastValue);
    }


    private void deployLoadUsersReceivedRequests() {
        tableViewReceivedRequests.addEventFilter(ScrollEvent.ANY, handlerScrollRequestsReceived);
    }


    private void initModelUsersSentRequests(){
        userPage.setPageUsersRequestSent(16);
        modelSentRequests.setAll(userPage.getSentRequestsPaged(0));
    }

    private void refreshModelSentRequests(){
        int lastValue=userPage.getPageUsersRequestSentSize();
        userPage.setPageUsersRequestSent(tableViewSentRequests.getItems().size());
        modelSentRequests.setAll(userPage.getSentRequestsPaged(0));
        userPage.setPageUsersRequestSent(lastValue);
    }

    private void deployLoadUsersSentRequests() {
        tableViewSentRequests.addEventFilter(ScrollEvent.ANY, handlerScrollRequestsSent);
    }



    private void initModelChat(){
        userPage.setPageMessages(8);
        modelMessages.clear();
        List<ReplyMessage> messages=userPage.getConversationPaged(selectedUserToChat.getId(),Long.MAX_VALUE);
        for(ReplyMessage mess:messages)
            modelMessages.add(0,mess);
    }

    private void deployLoadMessages(){
        listViewChat.addEventFilter(ScrollEvent.ANY,handlerScrollMessages);
    }

    //trebuie selectat pt bugul ala urat de la conversatii din ambele parti cate un user
    private void refreshConversation(){
        if(selectedUserToChat!=null)
            modelMessages.add(userPage.getLastMessageConversation(selectedUserToChat.getId()));
    }


    private void initModelUnfollowedEvents(){
        userPage.setPageUnfollowed(16);
        modelUnfollowedEvents.setAll(userPage.getUnfollowedEventsPaged(0));
    }

    private void refreshModelUnfollowedEvents(int numberOfLoadings){
        int lastValue=userPage.getPageUnfollowed();
        userPage.setPageUnfollowed(numberOfLoadings);
        modelUnfollowedEvents.setAll(userPage.getUnfollowedEventsPaged(0));
        userPage.setPageUnfollowed(lastValue);
    }

    private void deployLoadUnfollowedEvents(){
        tableViewUnfollowedEvents.addEventFilter(ScrollEvent.ANY,handlerScrollUnfollowed);
    }

    private void initModelFollowedEvents(){
        userPage.setPageFollowed(16);
        modelFollowedEvents.setAll(userPage.getFollowedEventsPaged(0));
        modelFollowedEvents.setAll(userPage.getFollowedEventsPaged(0));
    }

    private void refreshModelFollowedEvents(int numberOfLoadings){
        int lastValue=userPage.getPageFollowed();
        userPage.setPageFollowed(numberOfLoadings);
        modelFollowedEvents.setAll(userPage.getFollowedEventsPaged(0));
        userPage.setPageFollowed(lastValue);
    }

    private void deployLoadFollowedEvents(){
        tableViewFollowedEvents.addEventFilter(ScrollEvent.ANY,handlerScrollFollowed);
    }

    //------------------------------------------INIT MODELS--------------------------------------------//
    @FXML
    void handleLogout(ActionEvent event) {
        stage.close();
    }

    @FXML
    void handleShowFriends(ActionEvent event) {
        tabPaneTables.setVisible(true);
        tabPaneTables.getSelectionModel().select(0);
        initModelFriends();
        tableViewFriends.setItems(modelFriends);
        tabPane.getSelectionModel().select(1);
        AnchorPane.setTopAnchor(tabPaneTables,0.0);
        AnchorPane.setBottomAnchor(tabPaneTables,82.0);
        anchorInfo.setVisible(true);
        sendNotification();

    }

    @FXML
    void handleShowUsers(ActionEvent event) {
        initModelNonFriends();
        tableViewUsers.setItems(modelUsers);
        modelUsers.clear();
        AnchorPane.setBottomAnchor(tabPaneTables,0.0);
        AnchorPane.setTopAnchor(tabPaneTables,36.0);
        tabPaneTables.getSelectionModel().select(1);
        tabPaneTables.setVisible(true);
        buttonAddFriendAction.setVisible(false);
        textFieldSearchUser.clear();
        textFieldSearchUser.setVisible(true);
        tabPane.getSelectionModel().select(2);
        anchorInfo.setVisible(true);
        sendNotification();


    }

    @FXML
    void handleDeleteFriend(ActionEvent event){
        UtilizatorPrietenieDTO deletedFriend = tableViewFriends.getSelectionModel().getSelectedItem();
        if(deletedFriend!=null) {
            userPage.removeFriend(deletedFriend.getId());
            MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION, "Friend Deleted", "You are no longer friend with " + deletedFriend.getFirstName() + " " + deletedFriend.getLastName() + "!");
        }
        else
            MessageAlert.showErrorMessage(stage,"Please select a friend");

    }

    @FXML
    void handleFilterUsers() {
        userPage.setPageNonFriendsSize(17);
        List<Utilizator> usersList=userPage.getUserNonFriendsPaged(textFieldSearchUser.getText(),0);
        if(textFieldSearchUser.getText().isEmpty()) {
            modelUsers.clear();
            AnchorPane.setBottomAnchor(tabPaneTables,0.0);
            buttonAddFriendAction.setVisible(false);
        }
        else {
            modelUsers.setAll(usersList);
            if(modelUsers.size()!=0){
                buttonAddFriendAction.setVisible(true);
                AnchorPane.setBottomAnchor(tabPaneTables,82.0);
            }
            else {
                buttonAddFriendAction.setVisible(false);
                AnchorPane.setBottomAnchor(tabPaneTables,0.0);
            }
        }

    }

    @FXML
    void handleAddFriend(ActionEvent event){
        Utilizator selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            try {
                userPage.addFriend(selectedUser.getId());
                MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Request sent","The request was sent");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(stage,"Please select a user");
    }

    @FXML
    void handleShowRequests(ActionEvent event){
        tabPane.getSelectionModel().select(3);
        buttonRequestCancel.setVisible(false);
        buttonRequestAccept.setVisible(true);
        buttonRequestReject.setVisible(true);
        AnchorPane.setBottomAnchor(tabPaneTables,155.0);
        AnchorPane.setTopAnchor(tabPaneTables,0.0);
        tabPaneTables.getSelectionModel().select(2);
        tabPaneTables.setVisible(true);
        buttonChangeRequests.setText("Switch to sent requests");

        initModelUsersReceivedRequests();
        tableViewReceivedRequests.setItems(modelReceivedRequests);
        anchorInfo.setVisible(true);
        sendNotification();
    }

    @FXML
    void handleChangeRequests(ActionEvent event){

        if(buttonChangeRequests.getText().equals("Switch to sent requests")) {
            buttonRequestAccept.setVisible(false);
            buttonRequestReject.setVisible(false);
            buttonRequestCancel.setVisible(true);
            tabPaneTables.getSelectionModel().select(3);
            tabPaneTables.setVisible(true);
            initModelUsersSentRequests();
            tableViewSentRequests.setItems(modelSentRequests);
            buttonChangeRequests.setText("Switch to received requests");
        }
        else{
            buttonRequestAccept.setVisible(true);
            buttonRequestReject.setVisible(true);
            buttonRequestCancel.setVisible(false);
            tabPaneTables.getSelectionModel().select(2);
            tabPaneTables.setVisible(true);
            initModelUsersReceivedRequests();
            tableViewReceivedRequests.setItems(modelReceivedRequests);
            buttonChangeRequests.setText("Switch to sent requests");
        }
    }

    @FXML
    void handleAcceptRequest(ActionEvent event){
        Utilizator selectedUser = tableViewReceivedRequests.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            try {
                userPage.acceptRequest(selectedUser.getId());
                MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Request Accepted","You are now friend with "+selectedUser.getFirstName()+" "+selectedUser.getLastName()+"!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(stage,"Please select a user");

    }

    @FXML
    void handleRejectRequest(ActionEvent event){
        Utilizator selectedUser = tableViewReceivedRequests.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            try {
                userPage.removeFriend(selectedUser.getId());
                MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Request Rejected","You rejected the friend request of "+selectedUser.getFirstName()+" "+selectedUser.getLastName()+"!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(stage,"Please select a user");


    }

    @FXML
    void handleCancelRequest(ActionEvent event){
        Utilizator selectedUser = tableViewSentRequests.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            try {
                userPage.removeFriend(selectedUser.getId());
                MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Request Canceled","You canceled the friend request sent to "+selectedUser.getFirstName()+" "+selectedUser.getLastName()+"!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(stage, e.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(stage,"Please select a user");

    }

    @FXML
    void handleShowFriendsChat(ActionEvent event){
       // initModelFriends();
        tabPaneTables.setVisible(true);
        tabPaneTables.getSelectionModel().select(0);
        tableViewFriends.setItems(modelFriends);
        AnchorPane.setTopAnchor(tabPaneTables,0.0);
        AnchorPane.setBottomAnchor(tabPaneTables,0.0);
        tabPane.getSelectionModel().select(4);
        anchorInfo.setVisible(false);

    }

    @FXML
    void handleDisplayChat(MouseEvent event) {
        if(tabPane.getSelectionModel().getSelectedIndex()==4) {
            UtilizatorPrietenieDTO newSelectedUserToChat = tableViewFriends.getSelectionModel().getSelectedItem();
            if (selectedUserToChat != newSelectedUserToChat) {
                listViewChat.getItems().clear();
                selectedUserToChat = newSelectedUserToChat;
                initModelChat();
                buttonSendMessage.setText("Send");
                listViewChat.setItems(modelMessages);
            }
        }
    }

    @FXML
    void handleChangeButton(MouseEvent event) {
        if (listViewChat.getSelectionModel().getSelectedItem()==null) {
            buttonSendMessage.setText("Send");
        } else {
            buttonSendMessage.setText("Reply");
        }
    }


    //!!!!!!!!!!!!!!!!!!!BUG!!!!!!!!!!!!!!!!!
    //Cand un utilizator devine prieten cu un alt utilizator crapa mesajele for a reason(selectedUserToChat devine null in initModelChat)
    //Daca iasa din aplicatie si intra iara merge ok
    @FXML
    void handleSendMessage(ActionEvent event) {
        if (!textFieldTypeMessage.getText().isEmpty()) {

            if (listViewChat.getSelectionModel().getSelectedItems().size() == 0) {
                ArrayList<Utilizator> to = new ArrayList<>();
                to.add(userPage.findUser(selectedUserToChat.getId()).get());
                ReplyMessage mess = new ReplyMessage(userPage.getUser(), to, textFieldTypeMessage.getText(), null);
                userPage.sendMessage(mess);
                textFieldTypeMessage.clear();
            } else {
                ArrayList<Utilizator> to = new ArrayList<>();
                to.add(userPage.findUser(selectedUserToChat.getId()).get());
                ReplyMessage repliedMessage = listViewChat.getSelectionModel().getSelectedItem();
                ReplyMessage reply = new ReplyMessage(userPage.getUser(), to, textFieldTypeMessage.getText(), repliedMessage);
                userPage.replyMessage(reply);
                listViewChat.getSelectionModel().clearSelection();
                buttonSendMessage.setText("Send");
                textFieldTypeMessage.clear();

            }
        }
    }


    @FXML
    void handleShowReports(ActionEvent event){
     //   initModelFriends();
        tabPane.getSelectionModel().select(5);
        textFieldFileOutput.setEditable(false);
        tabPaneTables.getSelectionModel().select(0);
        textFieldPDFName.clear();
        textFieldFileOutput.clear();
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Display all your friends created and your received messages from a calendar period",
                        "Display all your received messages sent by a friend from a calendar period"
                );
        AnchorPane.setTopAnchor(tabPaneTables,0.0);
        AnchorPane.setBottomAnchor(tabPaneTables,0.0);
        tableViewFriends.setItems(modelFriends);
        comboBoxReport.setItems(options);
        tabPaneTables.setVisible(false);
        listViewReport.setVisible(false);
        comboBoxReport.getSelectionModel().clearSelection();
        anchorSaveFile.setVisible(false);
        buttonSaveFile.setVisible(false);
        anchorInfo.setVisible(false);

    }

    @FXML
    void handleGenerateReport(ActionEvent event) {
        LocalDate date1=datePicker1.getValue();
        LocalDate date2=datePicker2.getValue();
        listViewReport.getItems().clear();
        String errors="";
        if(comboBoxReport.getSelectionModel().isEmpty())
            errors+="Please select a report type\n";
        if(date1==null || date2==null)
            errors+="Please select both calendar periods";

        if(!errors.isEmpty())
            MessageAlert.showErrorMessage(stage,errors);
        else
        {

            if(comboBoxReport.getSelectionModel().getSelectedIndex()==0) {
                anchorSaveFile.setVisible(true);
                listViewReport.setVisible(true);
                listViewReport.getItems().add("The list of users below represents all the users you have become friends with between\n " + date1 + " and " + date2 + ":\n");
                boolean exists = false;
                for (UtilizatorPrietenieDTO u : userPage.getUserFriendBetweenDates(date1, date2)) {
                    listViewReport.getItems().add("         " + u + "\n");
                    exists = true;
                }
                if (!exists)
                    listViewReport.getItems().add("         There are no new friends created in this period.");

                listViewReport.getItems().add("Below are all the messages received between "+date1+" and "+date2+":\n");
                exists=false;
                for(UtilizatorPrietenieDTO u:userPage.getUserFriends()) {
                    List<ReplyMessage> messages=userPage.getConversationBetweenDates(u.getId(),date1,date2);
                    if(messages.size()!=0) {
                        exists=true;
                        listViewReport.getItems().add("\nMessages received from " + u.getFirstName() + " " + u.getLastName() + ":");
                        for (ReplyMessage m : messages) {
                            String text = "";
                            if (m.getRepliedMessage() == null) {
                                text = m.getDate().format(Constants.DATE_TIME_FORMATTER_NICE) +
                                        "\n         " + m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage() + "\n\n";
                            } else {
                                text = "    Replied at: " + m.getRepliedMessage().getMessage() + "     From: " + m.getRepliedMessage().getFrom().getLastName() + " " + m.getRepliedMessage().getFrom().getFirstName() + "\n         " +
                                        m.getDate().format(Constants.DATE_TIME_FORMATTER_NICE) +
                                        "\n         " + m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage() + "\n\n";
                            }
                            listViewReport.getItems().add("         " + text);
                        }
                    }
                }
                if(!exists)
                    listViewReport.getItems().add("         There are no new received messages in this period.");

            }
            else if(comboBoxReport.getSelectionModel().getSelectedIndex()==1){
                if(tableViewFriends.getSelectionModel().getSelectedItem()==null)
                    MessageAlert.showErrorMessage(stage,"Please select a friend from the table");
                else {
                    anchorSaveFile.setVisible(true);
                    listViewReport.setVisible(true);
                    UtilizatorPrietenieDTO otherUser = tableViewFriends.getSelectionModel().getSelectedItem();
                    listViewReport.getItems().add("Below are all the messages received from " + otherUser.getFirstName() + " " + otherUser.getLastName() + " between\n" + date1 + " and " + date2 + ":\n");
                    boolean exists = false;

                    List<ReplyMessage> messages = userPage.getConversationBetweenDates(otherUser.getId(), date1, date2);
                    if (messages.size() != 0) {
                        exists = true;
                        listViewReport.getItems().add("\nMessages received from " + otherUser.getFirstName() + " " + otherUser.getLastName() + ":");
                        for (ReplyMessage m : messages) {
                            String text = "";
                            if (m.getRepliedMessage() == null) {
                                text = m.getDate().format(Constants.DATE_TIME_FORMATTER_NICE) +
                                        "\n         " + m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage() + "\n\n";
                            } else {
                                text = "    Replied at: " + m.getRepliedMessage().getMessage() + "     From: " + m.getRepliedMessage().getFrom().getLastName() + " " + m.getRepliedMessage().getFrom().getFirstName() + "\n         " +
                                        m.getDate().format(Constants.DATE_TIME_FORMATTER_NICE) +
                                        "\n         " + m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage() + "\n\n";
                            }
                            listViewReport.getItems().add("         " + text);
                        }
                    }
                    if (!exists)
                        listViewReport.getItems().add("         There are no new received messages in this period.");
                }

            }
        }
    }

    @FXML
    void handleSaveFile(ActionEvent event){
        LocalDate date1=datePicker1.getValue();
        LocalDate date2=datePicker2.getValue();
        PDFGenerator documentPDF=new PDFGenerator(userPage);
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(textFieldFileOutput.getText()));
            document.open();
            documentPDF.addMetaData(document);
            documentPDF.addTitlePage(document, comboBoxReport.getSelectionModel().getSelectedItem());
            if(comboBoxReport.getSelectionModel().getSelectedIndex()==0) {
                documentPDF.addFriendships(document, userPage.getUser().getId(), date1, date2);
                documentPDF.addMessages(document, userPage.getUser().getId(), date1, date2);
                document.close();
            }
            else{
                documentPDF.addMessagesOneUser(document, userPage.getUser().getId(),tableViewFriends.getSelectionModel().getSelectedItem().getId(), date1, date2);
                document.close();
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Want to open the created PDF?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.initOwner(stage);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (Desktop.isDesktopSupported()) {
                    File myFile = new File(textFieldFileOutput.getText());
                    Desktop.getDesktop().open(myFile);
                }
            }
            textFieldPDFName.clear();
            textFieldFileOutput.clear();
            buttonSaveFile.setVisible(false);
            anchorSaveFile.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleBrowse(ActionEvent event){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if(selectedDirectory!=null) {
            if (textFieldPDFName.getText().isEmpty())
                textFieldFileOutput.setText(selectedDirectory.getAbsolutePath() + "\\Report.pdf");
            else
                textFieldFileOutput.setText(selectedDirectory.getAbsolutePath() + "\\" + textFieldPDFName.getText() + ".pdf");
        }
        if(!textFieldFileOutput.getText().isEmpty())
            buttonSaveFile.setVisible(true);


    }

    @FXML
    void handleSwitchReport(ActionEvent event){
        int choice=comboBoxReport.getSelectionModel().getSelectedIndex();
        if(choice==0)
            tabPaneTables.setVisible(false);
        else if(choice==1)
            tabPaneTables.setVisible(true);
        textFieldPDFName.clear();
        textFieldFileOutput.clear();
        buttonSaveFile.setVisible(false);
        anchorSaveFile.setVisible(false);
        listViewReport.getItems().clear();
        listViewReport.setVisible(false);
        datePicker1.getEditor().clear();
        datePicker2.getEditor().clear();
    }


    @FXML
    void handleShowEvents(ActionEvent event){
        initModelUnfollowedEvents();
        tableViewUnfollowedEvents.setItems(modelUnfollowedEvents);
        tabPane.getSelectionModel().select(6);
        tabPaneTables.getSelectionModel().select(4);
        tabPaneTables.setVisible(true);
        AnchorPane.setBottomAnchor(tabPaneTables,155.0);
        AnchorPane.setTopAnchor(tabPaneTables,0.0);
        buttonFollowEvent.setVisible(true);
        buttonUnfollowEvent.setVisible(false);
        tabPaneTables.setVisible(true);
        buttonChangeEvents.setText("Switch to followed events");
        listViewEvent.setVisible(false);
        anchorInfo.setVisible(false);
        listViewEvent.setVisible(false);
        anchorAddEvent.setVisible(true);
    }


    @FXML
    void handleAddEvent(ActionEvent event){
        String errors="";
        if(datePickerEventStart.getValue()==null || startSpinner.getValue()==null){
            errors+="Please select a starting date and hour for the event!\n";
        }
        if(datePickerEventEnd.getValue()==null || endSpinner.getValue()==null){
            errors+="Please select an ending date and hour for the event!\n";
        }
        if(textFieldEventName.getText().trim().length()==0){
            errors+="Please enter a title for the event!\n";
        }
        if(textAreaEventDescription.getText().trim().length()==0){
            errors+="Please enter a description for the event!\n";
        }
        if(errors.length()>0){
            MessageAlert.showErrorMessage(stage,errors);
            return;
        }
        LocalDate dateStart=datePickerEventStart.getValue();
        LocalTime timeStart=startSpinner.getValue();
        LocalDateTime dateTimeStart = LocalDateTime.of(dateStart,timeStart);
        LocalDate dateEnd=datePickerEventEnd.getValue();
        LocalTime timeEnd=endSpinner.getValue();
        LocalDateTime dateTimeEnd=LocalDateTime.of(dateEnd,timeEnd);
        String eventTitle=textFieldEventName.getText();
        String eventDescription=textAreaEventDescription.getText();
        Event eventCreated=new Event(userPage.getUser(),dateTimeStart,dateTimeEnd,eventTitle,eventDescription);
        try {
            userPage.createEvent(eventCreated);
            MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Success","Event created with success!");
        }
        catch (ServiceException e){
            MessageAlert.showErrorMessage(stage,e.getMessage());
        }

    }

    public void handleFollowEvent(ActionEvent event){
        Event selectedEvent=tableViewUnfollowedEvents.getSelectionModel().getSelectedItem();
        if(selectedEvent==null){
            MessageAlert.showErrorMessage(stage,"Please select an event!\n");
            return;
        }
        userPage.followEvent(selectedEvent.getId());
        MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Success","You are following now the event called '"+selectedEvent.getName()+"!\n");
    }

    public void handleUnfollowEvent(ActionEvent event){
        Event selectedEvent=tableViewFollowedEvents.getSelectionModel().getSelectedItem();
        if(selectedEvent==null){
            MessageAlert.showErrorMessage(stage,"Please select an event!\n");
            return;
        }
        userPage.unfollowEvent(selectedEvent.getId());
        MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Success","You unfollowed the event called '"+selectedEvent.getName()+"!\n");
    }

    @FXML
    void handleChangeEvents(ActionEvent event){

        if(buttonChangeEvents.getText().equals("Switch to followed events")) {
            buttonFollowEvent.setVisible(false);
            buttonUnfollowEvent.setVisible(true);
            tabPaneTables.getSelectionModel().select(5);
            tabPaneTables.setVisible(true);
            initModelFollowedEvents();
            tableViewFollowedEvents.setItems(modelFollowedEvents);
            buttonChangeEvents.setText("Switch to unfollowed events");
            listViewEvent.setVisible(false);
        }
        else{
            buttonFollowEvent.setVisible(true);
            buttonUnfollowEvent.setVisible(false);
            tabPaneTables.getSelectionModel().select(4);
            tabPaneTables.setVisible(true);
            initModelUnfollowedEvents();
            tableViewUnfollowedEvents.setItems(modelUnfollowedEvents);
            buttonChangeEvents.setText("Switch to followed events");
            listViewEvent.setVisible(false);
        }
    }

    @FXML
    public void handleShowParticipants(){
        if(buttonChangeEvents.getText().equals("Switch to followed events")) {
            boolean hasParticipants=false;
            Event selectedEvent = tableViewUnfollowedEvents.getSelectionModel().getSelectedItem();
            if (selectedEvent == null) {
                MessageAlert.showErrorMessage(stage, "Please select an event!\n");
                return;
            }
            anchorAddEvent.setVisible(false);
            listViewEvent.setVisible(true);
            listViewEvent.getItems().clear();
            for (Utilizator participant : userPage.getEventParticipants(selectedEvent.getId())) {
                hasParticipants=true;
                listViewEvent.getItems().add(participant.toString());
            }
            if(!hasParticipants)
                listViewEvent.getItems().add("There are no participants for this event at the moment");
        }
        else if(buttonChangeEvents.getText().equals("Switch to unfollowed events")){
            boolean hasParticipants=false;
            Event selectedEvent = tableViewFollowedEvents.getSelectionModel().getSelectedItem();
            if (selectedEvent == null) {
                MessageAlert.showErrorMessage(stage, "Please select an event!\n");
                return;
            }
            anchorAddEvent.setVisible(false);
            listViewEvent.setVisible(true);
            listViewEvent.getItems().clear();
            for (Utilizator participant : userPage.getEventParticipants(selectedEvent.getId())) {
                hasParticipants=true;
                listViewEvent.getItems().add(participant.toString());
            }
            if(!hasParticipants)
                listViewEvent.getItems().add("There are no participants for this event at the moment");
        }
    }

    @FXML
    void handleShowInfo(ActionEvent event){
        if(buttonChangeEvents.getText().equals("Switch to followed events")) {
            Event selectedEvent = tableViewUnfollowedEvents.getSelectionModel().getSelectedItem();
            if (selectedEvent == null) {
                MessageAlert.showErrorMessage(stage, "Please select an event!\n");
                return;
            }
            anchorAddEvent.setVisible(false);
            listViewEvent.setVisible(true);
            listViewEvent.getItems().clear();
            listViewEvent.getItems().add("Owner: "+selectedEvent.getOwner()+"\n");
            listViewEvent.getItems().add("Name: "+selectedEvent.getName()+"\n");
            listViewEvent.getItems().add("Starting date: "+selectedEvent.getStartDate().format(Constants.DATE_TIME_FORMATTER_HM)+"\n");
            listViewEvent.getItems().add("Ending date: "+selectedEvent.getEndDate().format(Constants.DATE_TIME_FORMATTER_HM)+"\n");
            listViewEvent.getItems().add("Description: "+selectedEvent.getDescription());


        }
        else if(buttonChangeEvents.getText().equals("Switch to unfollowed events")){
            Event selectedEvent = tableViewFollowedEvents.getSelectionModel().getSelectedItem();
            if (selectedEvent == null) {
                MessageAlert.showErrorMessage(stage, "Please select an event!\n");
                return;
            }
            anchorAddEvent.setVisible(false);
            listViewEvent.setVisible(true);
            listViewEvent.getItems().clear();
            listViewEvent.getItems().add("Owner: "+selectedEvent.getOwner()+"\n");
            listViewEvent.getItems().add("Name: "+selectedEvent.getName()+"\n");
            listViewEvent.getItems().add("Starting date: "+selectedEvent.getStartDate().format(Constants.DATE_TIME_FORMATTER_HM)+"\n");
            listViewEvent.getItems().add("Ending date: "+selectedEvent.getEndDate().format(Constants.DATE_TIME_FORMATTER_HM)+"\n");
            listViewEvent.getItems().add("Description: "+selectedEvent.getDescription());

        }
    }

    @FXML
    public void sendNotification(){
        String upcomingEvents="UPCOMING EVENTS IN THE NEXT 7 DAYS!\n\n";
        boolean hasEvents=false;
        for(Event event:userPage.getFollowedEvents()) {
            if (LocalDateTime.now().plusDays(7).isAfter(event.getStartDate())) {
                upcomingEvents += event;
                hasEvents=true;
            }
        }
        if(hasEvents) {
            listViewUpcoming.getItems().clear();
            listViewUpcoming.getItems().add(upcomingEvents);
        }
        else{
            listViewUpcoming.getItems().clear();
            listViewUpcoming.getItems().add("You don't have any upcoming events yet!\nGo check the Events tab if you are interested in them.");
        }
    }

    @FXML
    private void handleSwitchAddEvent(){
        anchorAddEvent.setVisible(true);
        listViewEvent.setVisible(false);
    }


    public void setStage(Stage newStage){
        this.stage=newStage;
    }


    @Override
    public void update(EventObserver event) {

    }


}
