package socialnetwork.controller;
import javafx.event.ActionEvent;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.domain.UserPage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.exceptions.ServiceException;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.UserDbRepository;
import socialnetwork.service.EventService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class LoginController {
    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    EventService eventService;

    @FXML
    private Button buttonSignIn;

    @FXML
    private Button buttonClose;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldPassword;

    @FXML
    private TextField textFieldFirstNameSignIn;

    @FXML
    private TextField textFieldLastNameSignIn;

    @FXML
    private TextField textFieldEmailSignIn;

    @FXML
    private TextField textFieldPasswordSignIn;

    @FXML
    private AnchorPane anchorLogin;

    @FXML
    private AnchorPane anchorSignin;



    @FXML
    void handleSignIn(ActionEvent event) {
        String email= textFieldEmail.getText();
        String password=textFieldPassword.getText();
        Optional<Utilizator> user=userService.getBypass(email,password);
        try{
            if(user.isPresent()) {
                showUserWindow(user.get().getId());
            }
            else
                MessageAlert.showErrorMessage(null,"Your login credentials don't match an account in our system.");
        }
        catch (IllegalArgumentException e){
            MessageAlert.showErrorMessage(null,"A number must be entered!");
        }
        finally {
            textFieldPassword.clear();
        }
    }

    @FXML
    void handleBackLogin(ActionEvent event){
        anchorLogin.setVisible(true);
        anchorSignin.setVisible(false);
        textFieldFirstNameSignIn.clear();
        textFieldLastNameSignIn.clear();
        textFieldEmailSignIn.clear();
        textFieldPasswordSignIn.clear();


    }

    @FXML
    void handleClose(MouseEvent event) {
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();

    }

    @FXML
    void handleCreateAccount(ActionEvent event){
        anchorLogin.setVisible(false);
        anchorSignin.setVisible(true);

    }

    @FXML
    void handleSignUp(ActionEvent event){
        String firstName=textFieldFirstNameSignIn.getText();
        String lastName=textFieldLastNameSignIn.getText();
        String email=textFieldEmailSignIn.getText();
        String password=textFieldPasswordSignIn.getText();
        Utilizator newUser=new Utilizator(firstName,lastName,email,password);
        if(userService.checkExistingEmail(email)) {
            MessageAlert.showErrorMessage(null, "This email is already registered!");
            return;
        }
        try{
            newUser.setId(userService.getLastID()+1);
            userService.addUtilizator(newUser);
            textFieldFirstNameSignIn.clear();
            textFieldLastNameSignIn.clear();
            textFieldEmailSignIn.clear();
            textFieldPasswordSignIn.clear();
            anchorLogin.setVisible(true);
            anchorSignin.setVisible(false);
        }
        catch (ServiceException s){
            MessageAlert.showErrorMessage(null,s.getMessage());
        }



    }


    public void setMultiService(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService, EventService eventService) {
        this.userService=userService;
        this.friendshipService=friendshipService;
        this.messageService=messageService;
        this.eventService=eventService;

    }


    public void showUserWindow(long userID) {
        try {
            //setup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/userView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            //stage
            Stage userStage=new Stage();
            userStage.setFullScreen(true);
            userStage.setFullScreenExitHint("");
            userStage.setTitle("User Window");
           // userStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene=new Scene(root);
            userStage.setScene(scene);
            UserController userController=loader.getController();
            Utilizator utilizator=userService.getUser(userID).get();
            UserPage userPage=new UserPage(userService,friendshipService,messageService,eventService,utilizator);
            userController.setMultiService(userPage);
            userController.setStage(userStage);
            userStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
