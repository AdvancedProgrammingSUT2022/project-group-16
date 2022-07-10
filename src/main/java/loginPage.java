import Controllers.RegisterController;
import Models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import Models.Menu.Menu;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDateTime;

public class loginPage extends Application {
    public TextField username;
    public TextField password;
    private final RegisterController registerController
            = new RegisterController();
    public VBox borderPane;

    @Override
    public void start(Stage stage) throws Exception {
        if(Menu.allUsers.size() == 0)
            registerController.updateDatabase(); //update arraylist of users and get old users
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

    public void checkInputs(MouseEvent mouseEvent) throws Exception {
        if(username.getText().equals("")) {
            if(borderPane.getChildren().size() == 7) {
                Text text = new Text();
                text.setText("please enter username");
                borderPane.getChildren().add(text);
                borderPane.getChildren().get(7).setStyle("-fx-fill: red");
            }
            else
                ((Text) borderPane.getChildren().get(borderPane.getChildren().size() - 1)).
                        setText("please enter username");
        }
        else if(password.getText().equals("")) {
            if(borderPane.getChildren().size() == 7) {
                Text text = new Text();
                text.setText("please enter password");
                borderPane.getChildren().add(text);
                borderPane.getChildren().get(7).setStyle("-fx-fill: red");
            }
            else
                ((Text) borderPane.getChildren().get(borderPane.getChildren().size() - 1)).
                        setText("please enter password");
        }
        else if(registerController.getUserByUsername(username.getText()) == null) {
            if(borderPane.getChildren().size() == 7) {
                Text text = new Text();
                text.setText("there is no user with this username");
                borderPane.getChildren().add(text);
                borderPane.getChildren().get(7).setStyle("-fx-fill: red");
            }
            else
                ((Text) borderPane.getChildren().get(borderPane.getChildren().size() - 1)).
                        setText("there is no user with this username");
        }
        else if(registerController.isPasswordCorrect(username.getText(), password.getText())) {
            if(borderPane.getChildren().size() == 7) {
                Text text = new Text();
                text.setText("password is incorrect");
                borderPane.getChildren().add(text);
                borderPane.getChildren().get(7).setStyle("-fx-fill: red");
            }
            else
                ((Text) borderPane.getChildren().get(borderPane.getChildren().size() - 1)).
                        setText("password is incorrect");
        }
        else {
            if(borderPane.getChildren().size() == 8)
                borderPane.getChildren().remove(borderPane.getChildren().size() - 1);
            Menu.loggedInUser = registerController.getUserByUsername(username.getText());
            LocalDateTime now = LocalDateTime.now();
            Menu.loggedInUser.setLastLogin(String.valueOf(Main.timeAndDate.format(now)));
            registerController.writeDataOnJson();
            MainMenu mainMenu = new MainMenu();
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            mainMenu.start(stage);
        }

    }
}
