import Controllers.GameController;
import Controllers.RegisterController;
import Controllers.Utilities.chatController;
import Models.Menu.Menu;
import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
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
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import server.chatServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatMenu extends Application {
    public Pane list;
    private final VBox titles = new VBox();
    private final VBox users = new VBox();
    private final VBox usersPhotos = new VBox();
    private final RegisterController registerController = new RegisterController();
    public static User sender = null;
    public static User receiver = null;
    private final chatController chatController = new chatController();
    public static final chatServer server = new chatServer();
    public static boolean isGameStarted = false;
    private final GameController gameController = GameController.getInstance();

    public Pane getList() {
        return list;
    }
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
        search.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String keyName = keyEvent.getCode().getName();
                if ("Enter".equals(keyName)) {
                    String username = search.getText();
                    search.setText(null);
                    for(User user : Menu.allUsers)
                        if(user.getUsername().equals(username) &&
                                !sender.getUsername().equals(user.getUsername()) &&
                                !sender.getPrivateChats().containsKey(user.getUsername())) {
                            while (list.getChildren().size() > 6) {
                                if(list.getChildren().get(list.getChildren().size() - 1).getClass() == ImageView.class)
                                    list.getChildren().remove(list.getChildren().size() - 1);
                                else
                                    list.getChildren().remove(0);
                            }
                            sender.getPrivateChats().put(username, new ArrayList<>());
                            user.getPrivateChats().put(sender.getUsername(), new ArrayList<>());
                            makeChat(sender, user);
                            receiver = user;
                        }
                }
            }
        });
    }
    private ImageView makePhoto(URL url) {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(url.toExternalForm()));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        return imageView;
    }
    private boolean containButton(String username) {
        for (Node node : ((VBox) list.getChildren().get(5)).getChildren())
            if (((Button) node).getText().equals(username))
                return true;
        return false;
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
                if (!(list.getChildren().get(2).getClass() == Label.class &&
                        ((Label) list.getChildren().get(2)).getText().equals("public chat")) &&
                        (receiver != null && !containButton(receiver.getUsername()))) {
                    Button tmp = new Button();
                    buttonStyle(tmp, receiver.getUsername());
                    ImageView imageView = makePhoto(receiver.getPhoto());
                    ((VBox) list.getChildren().get(5)).getChildren().add(tmp);
                    ((VBox) list.getChildren().get(6)).getChildren().add(imageView);
                    tmp.setOnMousePressed(mouseEvent -> {
                        while (list.getChildren().size() > 6) {
                            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == ImageView.class)
                                list.getChildren().remove(list.getChildren().size() - 1);
                            else
                                list.getChildren().remove(0);
                        }
                        receiver = registerController.getUserByUsername(tmp.getText());
                        makeChat(sender, receiver);
                    });
                }
                LocalDateTime now = LocalDateTime.now();
                if(list.getChildren().get(2).getClass() == Label.class &&
                        ((Label) list.getChildren().get(2)).getText().equals("public chat")) {
                    String message = sender.getUsername() + ": " + typeMessage.getText() + " - "
                            + now.toString().substring(11,19);
                    chatController.sendPublicMessage(sender, message, server);
                    Label label = new Label();
                    messageStyleSender(label, server.getPublicChats().get(server.getPublicChats().size() - 1).getMessage());
                    ((VBox) list.getChildren().get(1)).getChildren().add(label);
                }
                else {
                    chatController.sendMessage(sender, receiver, typeMessage.getText() +
                            " - " + now.toString().substring(11,19));
                    registerController.writeDataOnJson();
                    ArrayList<Message> messages = sender.getPrivateChats().get(receiver.getUsername());
                    Label label = new Label();
                    messageStyleSender(label, messages.get(messages.size() - 1).getMessage());
                    ((VBox) list.getChildren().get(1)).getChildren().add(label);
                }
                typeMessage.setText(null);
            }

        });
    }
    private void makePublicChat() {
        VBox senderChats = new VBox();
        VBox receiverChats = new VBox();
        ArrayList<publicMessage> messages = server.getPublicChats();
        for(publicMessage message : messages) {
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
        list.getChildren().add(0,senderChats);
        list.getChildren().add(0,receiverChats);
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
            makePublicChat();
        });
        users.getChildren().add(button);//public chat button

        for(String user : sender.getPrivateChats().keySet()) {
            Button tmp = new Button();
            tmp.setOnMousePressed(mouseEvent -> {
                while (list.getChildren().size() > 6) {
                    if(list.getChildren().get(list.getChildren().size() - 1).getClass() == ImageView.class)
                        list.getChildren().remove(list.getChildren().size() - 1);
                    else
                        list.getChildren().remove(0);
                }
                receiver = registerController.getUserByUsername(user);
                makeChat(sender, receiver);
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
            sender = null;
            receiver = null;
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
    public void makeChat(User sender, User receiver) {
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
        //delete -s and -d from receiverChats
        int i = receiverChats.getChildren().size() - 1;
        while (i >= 0)
        {
            if (((Label) receiverChats.getChildren().get(i)).getText().endsWith(" - s") ||
                    ((Label) receiverChats.getChildren().get(i)).getText().endsWith(" - d")) {
                String message = ((Label) receiverChats.getChildren().get(i)).getText();
                ((Label) receiverChats.getChildren().get(i)).setText(message.substring(0, message.length() - 4));
                registerController.writeDataOnJson();
            }
            i--;
        }
        int j = receiver.getPrivateChats().get(sender.getUsername()).size() - 1;
        while (j >= 0) {
            String message = receiver.getPrivateChats().get(sender.getUsername()).get(j).getMessage();
            if(message.endsWith(" - d")) {
                receiver.getPrivateChats().get(sender.getUsername()).get(j).
                        setMessage(message.substring(0, message.length() - 4) + " - s");
                registerController.writeDataOnJson();
            }
            j--;
        }
        deleteOrEditMessage(senderChats, receiver.getUsername());
        list.getChildren().add(0,nameOfReceiver(receiver.getUsername()));
        list.getChildren().add(photoOfReceiver(receiver.getPhoto().toString()));
        list.getChildren().add(0,senderChats);
        list.getChildren().add(0,receiverChats);
    }
    private void chooseButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #000086;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 20;" +
                "-fx-pref-width: 300;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-pref-height: 40");
        button.setOnMouseMoved(mouseEvent -> button.setStyle("-fx-background-color: #2929ff;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 20;" +
                "-fx-pref-width: 300;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-pref-height: 40"));
        button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-background-color: #000086;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 20;" +
                "-fx-pref-width: 300;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-pref-height: 40"));
    }
    private void deleteOrEditMessage(VBox box, String user) {
        VBox choose = new VBox();
        Button edit = new Button();
        edit.setText("edit");
        chooseButtonStyle(edit);
        Button delete1 = new Button();
        delete1.setText("delete for me");
        chooseButtonStyle(delete1);
        Button delete2 = new Button();
        chooseButtonStyle(delete2);
        delete2.setText("delete for me and " + user);

        choose.getChildren().add(edit);
        choose.getChildren().add(delete1);
        choose.getChildren().add(delete2);
        choose.setSpacing(5);
        choose.setAlignment(Pos.CENTER);
        choose.setPrefWidth(350);
        choose.setPrefHeight(200);
        choose.setLayoutX(565);
        choose.setLayoutY(285);
        choose.setStyle("-fx-background-color: #5656fd;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 4;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8");
        for (int k = 0; k < box.getChildren().size(); k++) {
            int flag = k;
            (box.getChildren().get(k)).setOnMousePressed(new EventHandler<MouseEvent>() {
                final String message = ((Label) (box.getChildren().get(flag))).getText();
                @Override
                public void handle(MouseEvent mouseEvent) {
                    list.getChildren().add(choose);
                    for (int i = 0; i < list.getChildren().size() - 1; i++)
                        list.getChildren().get(i).setDisable(true);
                    choose.getChildren().get(0).setOnMousePressed(mouseEvent1 -> {
                        TextField textField = new TextField();
                        textField.setStyle("-fx-border-width: 4;" +
                                "-fx-border-color: black;" +
                                "-fx-background-color: #6868f3;" +
                                "-fx-pref-width: 200");
                        textField.setPromptText("new message...");
                        textField.setLayoutX(640);
                        textField.setLayoutY(225);
                        choose.setDisable(true);
                        list.getChildren().add(textField);
                        textField.setOnKeyPressed(keyEvent -> {
                            String keyName = keyEvent.getCode().getName();
                            if (keyName.equals("Enter")) {
                                choose.setDisable(false);
                                for (int i = 0; i < list.getChildren().size(); i++)
                                    list.getChildren().get(i).setDisable(false);
                                String result = textField.getText();
                                list.getChildren().remove(list.getChildren().size() - 1);
                                list.getChildren().remove(list.getChildren().size() - 1);
                                String finalMessage = result + message.substring(message.length() - 14);
                                ((Label) box.getChildren().get(flag)).setText(finalMessage);
                                sender.getPrivateChats().get(user).get(flag).setMessage(finalMessage);
                                registerController.getUserByUsername(user).getPrivateChats().get(sender.getUsername())
                                        .get(flag).setMessage(finalMessage);
                                registerController.writeDataOnJson();
                            }

                        });
                    }); //edit message
                    choose.getChildren().get(1).setOnMousePressed(mouseEvent1 -> {
                        list.getChildren().remove(choose);
                        for (int i = 0; i < list.getChildren().size(); i++)
                            list.getChildren().get(i).setDisable(false);
                        ((Label) box.getChildren().get(flag)).setText("#deleted");
                        sender.getPrivateChats().get(user).get(flag).setMessage("#deleted");
                        registerController.writeDataOnJson();
                    }); //delete message for me
                    choose.getChildren().get(2).setOnMousePressed(mouseEvent1 -> {
                        list.getChildren().remove(choose);
                        for (int i = 0; i < list.getChildren().size(); i++)
                            list.getChildren().get(i).setDisable(false);
                        ((Label) box.getChildren().get(flag)).setText("#deleted");
                        sender.getPrivateChats().get(user).get(flag).setMessage("#deleted");
                        registerController.getUserByUsername(user).getPrivateChats().get(sender.getUsername()).get(flag).setMessage("#deleted");
                        registerController.writeDataOnJson();
                    }); //delete message for me and user
                }
            });
        }
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
        String lastSeen;
        if(!username.equals("public chat")) {
            lastSeen = registerController.getUserByUsername(username).getLastLogin();
            label.setText(username + " - last login: " + lastSeen);
        }
        else
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
        if(!isGameStarted) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
        }
        else
            ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
    }
    @Override
    public void start(Stage stage) throws Exception {
        if (sender == null)
            sender = Menu.loggedInUser;
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/chatMenu.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
