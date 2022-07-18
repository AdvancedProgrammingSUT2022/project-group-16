import Controllers.RegisterController;
import Models.Menu.Menu;
import Models.User;
import Models.chat.Message;
import Models.chat.Request;
import Models.chat.Respond;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

public class MainMenu extends Application {
    public Pane list;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/mainMenu.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void exit() {
        Platform.exit();
    }

    public void logout(MouseEvent mouseEvent) throws Exception {
        Menu.loggedInUser = null;
        loginPage loginPage = new loginPage();
        loginPage.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
    public void chatMenu(MouseEvent mouseEvent) throws Exception {
        ChatMenu chatMenu = new ChatMenu();
        chatMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());

    }
    public void profileMenu(MouseEvent mouseEvent) throws Exception {
        ProfileMenu profileMenu = new ProfileMenu();
        profileMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void newGame(MouseEvent mouseEvent) throws Exception {
        NewGame newGame = new NewGame();
        newGame.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void scoreBoard(MouseEvent mouseEvent) throws Exception {
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void loadGame(MouseEvent mouseEvent) throws Exception {
        LoadGame loadGame = new LoadGame();
        loadGame.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
