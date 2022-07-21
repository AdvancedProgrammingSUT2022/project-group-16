import IO.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;

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
