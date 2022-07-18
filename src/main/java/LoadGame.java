import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;

public class LoadGame extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/loadGame.fxml").toExternalForm()))));

        stage.show();
    }

    public void backToMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
