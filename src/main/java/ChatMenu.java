import Controllers.RegisterController;
import Controllers.Utilities.chatController;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    private User receiver = null;
    private final chatController chatController = new chatController();

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
    private void typeMessageStyle(TextField typeMessage) {
        typeMessage.setPromptText("type here...");
        typeMessage.setStyle("-fx-pref-width: 980;" +
                "-fx-pref-height: 45");
        typeMessage.setLayoutX(300);
        typeMessage.setLayoutY(675);
        typeMessage.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if ("Enter".equals(keyName)) {
                chatController.sendMessage(Menu.loggedInUser, receiver, typeMessage.getText());
                registerController.writeDataOnJson();
                int length = list.getChildren().size();
                ArrayList<Message> messages = Menu.loggedInUser.getPrivateChats().get(receiver.getUsername());
                Label label = new Label();
                messageStyleSender(label, messages.get(messages.size() - 1).getMessage());
                ((VBox) list.getChildren().get(1)).getChildren().add(label);
                typeMessage.setText(null);
            }

        });
    }
    public void initialize() throws MalformedURLException {
        users.setAlignment(Pos.CENTER);
        usersPhotos.setSpacing(11);//figure places

        chatLabel();
        searchTextField();//titles

        Button button = new Button();
        ImageView publicChat = makePhoto(new URL(getClass().getResource(
                "photos/chatIcons/public-chat.jpg").toExternalForm()));
        usersPhotos.getChildren().add(publicChat);
        buttonStyle(button, "public chat");
        button.setOnMousePressed(mouseEvent -> {
            while (list.getChildren().size() > 6) {
                if(list.getChildren().get(list.getChildren().size() - 1).getClass() == ImageView.class)
                    list.getChildren().remove(list.getChildren().size() - 1);
                else
                    list.getChildren().remove(0);
            }
            list.getChildren().add(0,nameOfReceiver("public chat"));
            list.getChildren().add(photoOfReceiver("photos/chatIcons/public-chat.jpg"));
        });
        users.getChildren().add(button);//public chat button

        for(String user : Menu.loggedInUser.getPrivateChats().keySet()) {
            Button tmp = new Button();
            tmp.setOnMousePressed(mouseEvent -> {
                System.out.println(list.getChildren().size());
                while (list.getChildren().size() > 6) {
                    if(list.getChildren().get(list.getChildren().size() - 1).getClass() == ImageView.class)
                        list.getChildren().remove(list.getChildren().size() - 1);
                    else
                        list.getChildren().remove(0);
                }
                makeChat(Menu.loggedInUser, registerController.getUserByUsername(user));
                receiver = registerController.getUserByUsername(user);
            });
            buttonStyle(tmp, user);
            ImageView imageView = makePhoto(registerController.getUserByUsername(user).getPhoto());
            users.getChildren().add(tmp);
            usersPhotos.getChildren().add(imageView);
        }//friend's buttons

        TextField typeMessage = new TextField();
        typeMessageStyle(typeMessage);//type textField

        Button back = new Button();
        buttonStyle(back, "back");
        back.setLayoutY(670);
        back.setOnMousePressed(mouseEvent -> {
            try {
                backToMainMenu(mouseEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }); //back button

        //add boxes to main Pane
        usersPhotos.setLayoutY(110);
        usersPhotos.setLayoutX(4);
        users.setLayoutY(103);
        list.getChildren().add(titles);
        list.getChildren().add(users);
        list.getChildren().add(usersPhotos);
        list.getChildren().add(typeMessage);
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
    private void makeChat(User sender, User receiver) {
        VBox senderChats = new VBox();
        VBox receiverChats = new VBox();
        for(Message message : sender.getPrivateChats().get(receiver.getUsername())) {
            Label label = new Label();
            Label tmp = new Label();
            tmp.setStyle("-fx-border-width: 2;" +
                    "-fx-border-color: rgba(255,0,0,0);" +
                    "-fx-font-size: 20");
            if(message.getSender().equals(sender.getUsername())) {
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
        scrollVbox(senderChats, receiverChats);
        senderChats.setSpacing(5);
        senderChats.setAlignment(Pos.TOP_RIGHT);
        senderChats.setPrefWidth(450);
        senderChats.setLayoutX(790);
        senderChats.setLayoutY(90);
        receiverChats.setPrefWidth(450);
        receiverChats.setSpacing(5);
        receiverChats.setLayoutX(325);
        receiverChats.setLayoutY(90);
        receiverChats.setAlignment(Pos.TOP_LEFT);
        list.getChildren().add(0,nameOfReceiver(receiver.getUsername()));
        list.getChildren().add(photoOfReceiver(receiver.getPhoto().toString()));
        list.getChildren().add(0,senderChats);
        list.getChildren().add(0,receiverChats);
    }

    private void scrollVbox(VBox vBox1, VBox vBox2) {
        vBox1.setOnScroll((ScrollEvent event) -> {
            double yScale = 30;
            double deltaY = event.getDeltaY();
            if (deltaY < 0)
                yScale *= -1;
            if((Math.max(vBox1.getLayoutY() + vBox1.getHeight(), vBox2.getLayoutY() + vBox2.getHeight()) > 675 && yScale < 0 ||
                    Math.min(vBox1.getLayoutY(), vBox2.getLayoutY()) < 80 && yScale > 0)) {
                vBox1.setLayoutY(vBox1.getLayoutY() + yScale);
                vBox2.setLayoutY(vBox2.getLayoutY() + yScale);
            }
        });
        vBox2.setOnScroll((ScrollEvent event) -> {
            double yScale = 30;
            double deltaY = event.getDeltaY();
            if (deltaY < 0)
                yScale *= -1;
            if((Math.max(vBox1.getLayoutY() + vBox1.getHeight(), vBox2.getLayoutY() + vBox2.getHeight()) > 675 && yScale < 0 ||
                    Math.min(vBox1.getLayoutY(), vBox2.getLayoutY()) < 80 && yScale > 0)) {
                vBox1.setLayoutY(vBox1.getLayoutY() + yScale);
                vBox2.setLayoutY(vBox2.getLayoutY() + yScale);
            }
        });
    }
    private Label nameOfReceiver(String username) {
        Label label = new Label();
        label.setText(username);
        label.setStyle("-fx-text-fill: #ffffff;" +
                "-fx-background-color: rgba(0,61,6,0.97);" +
                "-fx-pref-width: 980;" +
                "-fx-pref-height: 70;" +
                "-fx-alignment: center;" +
                "-fx-font-size: 30;" +
                "-fx-border-width: 5;" +
                "-fx-border-color: black;");
        label.setLayoutX(300);
        return label;
    }
    private ImageView photoOfReceiver(String path) {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(path));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        imageView.setX(310);
        imageView.setY(10);
        return imageView;
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
