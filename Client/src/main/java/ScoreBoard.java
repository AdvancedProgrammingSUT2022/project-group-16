
import IO.Client;
import IO.Request;
import IO.Response;
import Models.Terrain.Tile;
import Models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class ScoreBoard extends Application {
    //server stuff
    Socket socket = Client.socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Request request = new Request();
    Response response;

    //update stuff
    private boolean isRPressed = false;
    private boolean isShiftPressed = false;

    //define main pane
    public Pane list;
    private final VBox vBox = new VBox();
    private final VBox photos = new VBox();

    public void initialize() {
        loadScoreBoard();

        //cheat code
        list.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.R)
                isRPressed = true;
            if(key.getCode() == KeyCode.SHIFT)
                isShiftPressed = true;
            if(isShiftPressed && isRPressed) {
                updateUsersLabel();
            }
        });
        list.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            if(key.getCode() == KeyCode.R)
                isRPressed = false;
            if(key.getCode() == KeyCode.SHIFT)
                isShiftPressed = false;
        });
    }

    private void loadScoreBoard() {
        //server
        try
        {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //scroll
        vBox.setOnScroll((ScrollEvent event) -> {
            double yScale = 30;
            double deltaY = event.getDeltaY();
            if (deltaY < 0)
                yScale *= -1;
            if((vBox.getLayoutY() + vBox.getHeight() > 650 && yScale < 0) || vBox.getLayoutY() < 40 && yScale > 0) {
                vBox.setLayoutY(vBox.getLayoutY() + yScale);
                photos.setLayoutY(photos.getLayoutY() + yScale);
            }
        });

        updateUsersLabel();
    }

    private void updateUsersLabel()
    {
        vBox.getChildren().clear();
        photos.getChildren().clear();
        list.getChildren().remove(photos);
        list.getChildren().remove(vBox);


        //avatars
        photos.setAlignment(Pos.CENTER);
        photos.setSpacing(20);
        vBox.setSpacing(90);

        //sort users on 1-score 2-lastTimeOfWin 3-username
        ArrayList<User> allUsers = Client.getInstance().getAllUsers();
        for(User user : allUsers)
            user.setScore(user.getScore() * -1);
        allUsers.sort(Comparator.comparing(User::getScore).thenComparing(User::getLastTimeOfWin).thenComparing(User::getUsername));
        for(User user : allUsers)
            user.setScore(user.getScore() * -1);

        //server
        request.setAction("user online");
        try
        {
            dataOutputStream.writeUTF(request.toJson());
            dataOutputStream.flush();

            response = Response.fromJson(dataInputStream.readUTF());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //add labels
        for (int i = 0; i < allUsers.size(); i++) {
            User user = allUsers.get(i);

            Label label = new Label();
            label.setText((i + 1) + " - username: " +
                    user.getUsername() + " - Score: " +
                    user.getScore() + " - last time of winning: " +
                    user.getLastTimeOfWin() + " - last login: " + user.getLastLogin());

            //label styles (logged in, online, else)
            if(Client.getInstance().getLoggedInUser().getUsername().equals(user.getUsername()))
                setLoggedInLabelStyle(label);
            else if (response.getParams().containsKey(user.getUsername()))
                setOnlineLabelStyle(label);
            else
                setLabelStyle(label);

            //add box to pane
            vBox.getChildren().add(label);

            //add avatar
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(String.valueOf(user.getPhoto())));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            photos.getChildren().add(imageView);
        }

        //add nodes to main pane
        list.getChildren().add(photos);
        list.getChildren().add(vBox);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(130);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(40);
        list.getChildren().get(list.getChildren().size() - 2).setLayoutX(1100);
        list.getChildren().get(list.getChildren().size() - 2).setLayoutY(8);
    }


    //styles
    private void setLabelStyle(Label label) {
        label.setStyle("-fx-font-size: 24; " +
                "-fx-text-fill: white");
    }
    private void setLoggedInLabelStyle(Label label) {
        label.setStyle("-fx-font-size: 24; " +
                "-fx-text-fill: white;" +
                "-fx-background-color: black;" +
                "-fx-background-radius: 4;" +
                "-fx-border-color: red;" +
                "-fx-border-width: 3");
    }
    private void setOnlineLabelStyle(Label label) {
        label.setStyle("-fx-font-size: 24; " +
                "-fx-text-fill: white;" +
                "-fx-background-color: #0022ff;" +
                "-fx-background-radius: 4;" +
                "-fx-border-color: #000000;" +
                "-fx-border-width: 3");
    }

    //menu methods
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/scoreBoard.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void backToMainMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
