import IO.Client;
import IO.Response;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

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
        Client.getInstance().logout();
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
//        NewGame newGame = new NewGame();
//        newGame.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void multiplayer(MouseEvent mouseEvent) throws Exception
    {
        MultiplayerMenu multiplayerMenu = new MultiplayerMenu();
        multiplayerMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
    public void scoreBoard(MouseEvent mouseEvent) throws Exception {
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void loadGame(MouseEvent mouseEvent) throws Exception {
//        LoadGame loadGame = new LoadGame();
//        loadGame.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void friendsMenu(MouseEvent mouseEvent) throws Exception {
        FriendShipMenu friendShipMenu = new FriendShipMenu();
        friendShipMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
