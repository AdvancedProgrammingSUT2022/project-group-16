import Controllers.RegisterController;
import Models.User;
import Models.chat.Message;
import enums.registerEnum;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import Models.Menu.Menu;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RegisterPage extends Application {
    private final RegisterController registerController = new RegisterController();
    public TextField username;
    public TextField password;
    public VBox VBox;
    public TextField nickname;
    public Pane list;
    private final URL guestImage = getClass().getResource("photos/profilePhotos/guest.jpg");

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/registerPage.fxml").toExternalForm()))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void checkInputs(MouseEvent mouseEvent) throws Exception {
        String message;
        if(username.getText().equals("")) {
            if(VBox.getChildren().size() == 8) {
                Text text = new Text();
                text.setText("please enter username");
                VBox.getChildren().add(text);
                VBox.getChildren().get(8).setStyle("-fx-fill: red");
            }
            else
                ((Text) VBox.getChildren().get(VBox.getChildren().size() - 1)).
                        setText("please enter username");
        }
        else if(nickname.getText().equals("")) {
            if(VBox.getChildren().size() == 8) {
                Text text = new Text();
                text.setText("please enter nickname");
                VBox.getChildren().add(text);
                VBox.getChildren().get(8).setStyle("-fx-fill: red");
            }
            else
                ((Text) VBox.getChildren().get(VBox.getChildren().size() - 1)).
                        setText("please enter nickname");
        }
        else if(password.getText().equals("")) {
            if(VBox.getChildren().size() == 8) {
                Text text = new Text();
                text.setText("please enter password");
                VBox.getChildren().add(text);
                VBox.getChildren().get(8).setStyle("-fx-fill: red");
            }
            else
                ((Text) VBox.getChildren().get(VBox.getChildren().size() - 1)).
                        setText("please enter password");
        }
        else if(!(message = registerController.createUser(username.getText(), password.getText(),
                nickname.getText(), guestImage)).equals(registerEnum.successfulCreate.regex)) {
            if(VBox.getChildren().size() == 8) {
                Text text = new Text();
                text.setText(message);
                VBox.getChildren().add(text);
                VBox.getChildren().get(8).setStyle("-fx-fill: red");
            }
            else
                ((Text) VBox.getChildren().get(VBox.getChildren().size() - 1)).
                        setText(message);
        }
        else {
            if(VBox.getChildren().size() == 9)
                VBox.getChildren().remove(VBox.getChildren().size() - 1);
            Menu.loggedInUser = registerController.getUserByUsername(username.getText());
            LocalDateTime now = LocalDateTime.now();
            Menu.loggedInUser.setLastLogin(Main.timeAndDate.format(now));
            for(User user : Menu.allUsers)
                if(user != Menu.loggedInUser) {
                    Menu.loggedInUser.getPrivateChats().put(user.getUsername(), new ArrayList<>());
                    user.getPrivateChats().put(Menu.loggedInUser.getUsername(), new ArrayList<>());
                }
            registerController.writeDataOnJson();
            MainMenu mainMenu = new MainMenu();
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            mainMenu.start(stage);
        }
            
    }
}
