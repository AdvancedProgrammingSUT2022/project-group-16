import Controllers.RegisterController;
import Models.Menu.Menu;
import Models.User;
import Models.chat.Message;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChatMenu extends Application {
    public Pane list;
    private final VBox titles = new VBox();
    private final VBox users = new VBox();
    private final VBox usersPhotos = new VBox();
    private final RegisterController registerController = new RegisterController();

    private void buttonStyle(Button button, String name) {
        button.setText(name);
        button.setOnMouseMoved(mouseEvent -> button.setStyle("-fx-pref-width: 300; " +
                "-fx-pref-height: 50;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: rgba(0,122,13,0.97);" +
                "-fx-font-size: 30;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2"));
        button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-pref-width: 300; " +
                "-fx-pref-height: 50;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: rgba(0,61,6,0.97);" +
                "-fx-font-size: 30;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2"));
        button.setStyle("-fx-pref-width: 300; " +
                "-fx-pref-height: 50;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: rgba(0,61,6,0.97);" +
                "-fx-font-size: 30;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2");
    }

    private void chatLabel() {
        Label label = new Label("Chats");
        label.setStyle("-fx-text-fill: #ffffff;" +
                "-fx-background-color: rgba(0,61,6,0.97);" +
                "-fx-pref-width: 300;" +
                "-fx-pref-height: 70;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 30;" +
                "-fx-border-width: 5;" +
                "-fx-border-color: black");
        titles.getChildren().add(label);
    }
    private void searchTextField() {
        TextField search = new TextField();
        search.setPromptText("search...");
        search.setStyle("-fx-max-width: 300;" +
                "-fx-pref-height: 35;");
        titles.getChildren().add(search);
    }
    private ImageView makePhoto(URL url) {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(url.toExternalForm()));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        return imageView;
    }
    public void initialize() throws MalformedURLException {
        users.setAlignment(Pos.CENTER);
        usersPhotos.setSpacing(11);
        chatLabel();
        searchTextField();
        Button button = new Button();
        ImageView publicChat = makePhoto(new URL(getClass().getResource(
                "photos/chatIcons/public-chat.jpg").toExternalForm()));
        usersPhotos.getChildren().add(publicChat);
        buttonStyle(button, "public chat");
        users.getChildren().add(button);
        for(String user : Menu.loggedInUser.getPrivateChats().keySet()) {
            Button tmp = new Button();
            tmp.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    makeChat(registerController.getUserByUsername(user));
                }
            });
            buttonStyle(tmp, user);
            ImageView imageView = makePhoto(registerController.getUserByUsername(user).getPhoto());
            users.getChildren().add(tmp);
            usersPhotos.getChildren().add(imageView);
        }
        Button back = new Button();
        buttonStyle(back, "back");
        back.setLayoutY(670);
        back.setOnMousePressed(mouseEvent -> {
            try {
                backToMainMenu(mouseEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        usersPhotos.setLayoutY(110);
        usersPhotos.setLayoutX(4);
        users.setLayoutY(103);
        list.getChildren().add(titles);
        list.getChildren().add(users);
        list.getChildren().add(usersPhotos);
        list.getChildren().add(back);
    }
    private void messageStyleSender(Label label, String message) {
        label.setText(message);
        label.autosize();
        label.setStyle("-fx-border-radius: 5;" +
                "-fx-border-width: 2;" +
                "-fx-border-color: black;" +
                "-fx-background-color: #a7ffa7;" +
                "-fx-text-fill: black;" +
                "-fx-font-size: 20;" +
                "-fx-background-radius: 8;");
    }
    private void messageStyleReceiver(Label label, String message) {
        label.setText(message);
        label.autosize();
        label.setStyle("-fx-border-radius: 5;" +
                "-fx-border-width: 2;" +
                "-fx-border-color: black;" +
                "-fx-background-color: #fcfcfc;" +
                "-fx-text-fill: black;" +
                "-fx-font-size: 20;" +
                "-fx-background-radius: 8");
    }
    private void makeChat(User receiver) {
        User sender = Menu.loggedInUser;
        VBox senderChats = new VBox();
        VBox receiverChats = new VBox();
        for(Message message : Menu.loggedInUser.getPrivateChats().get(receiver.getUsername())) {
            Label label = new Label();
            Label tmp = new Label();
            tmp.setStyle("-fx-border-width: 2;" +
                    "-fx-border-color: rgba(255,0,0,0);" +
                    "-fx-font-size: 20");
            if(message.getSender() == sender) {
                messageStyleSender(label, message.getMessage());
                senderChats.getChildren().add(label);
                receiverChats.getChildren().add(tmp);
            }
            else {
                messageStyleReceiver(label, message.getMessage());
                receiverChats.getChildren().add(label);
                senderChats.getChildren().add(tmp);
            }
        }
        senderChats.setSpacing(5);
        senderChats.setAlignment(Pos.TOP_LEFT);
        senderChats.setLayoutX(350);
        receiverChats.setSpacing(5);
        receiverChats.setLayoutX(1100);
        receiverChats.setAlignment(Pos.TOP_RIGHT);
        list.getChildren().add(senderChats);
        list.getChildren().add(receiverChats);
    }
    public void backToMainMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/chatMenu.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
