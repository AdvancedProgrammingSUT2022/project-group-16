import Models.Menu.Menu;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;

public class MainMenu extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/mainMenu.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void exit(MouseEvent mouseEvent) {
        Platform.exit();
    }

    public void logout(MouseEvent mouseEvent) throws Exception {
        Menu.loggedInUser = null;
        loginPage loginPage = new loginPage();
        loginPage.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void profileMenu(MouseEvent mouseEvent) throws Exception {
        ProfileMenu profileMenu = new ProfileMenu();
        profileMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void newGame(MouseEvent mouseEvent) { //TODO: new game menu
    }
}
