import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;

public class ProfileMenu extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/profileMenu.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void backToMainMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void changeNicknameMenu(MouseEvent mouseEvent) throws Exception {
        ChangeNickname changeNickname = new ChangeNickname();
        changeNickname.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void changePasswordMenu(MouseEvent mouseEvent) throws Exception {
        ChangePassword changePassword = new ChangePassword();
        changePassword.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
