import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.net.URL;

public class WelcomePage extends Application {
    @FXML
    public Pane list;
    public void enterLoginPage(MouseEvent mouseEvent) throws Exception {
        loginPage loginPage = new loginPage();
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        loginPage.start(stage);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/welcomePage.fxml").toExternalForm()))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
