import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;

public class loginPage extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/loginPage.fxml").toExternalForm()))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void enterRegisterPage(MouseEvent mouseEvent) throws Exception {
        RegisterPage registerPage = new RegisterPage();
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        registerPage.start(stage);
    }
}
