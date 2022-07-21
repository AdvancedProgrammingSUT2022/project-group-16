import IO.Client;
import IO.Response;
import Models.User;
import enums.registerEnum;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;

public class RegisterPage extends Application {
    public TextField username;
    public TextField password;
    public VBox VBox;
    public TextField nickname;
    public Pane list;

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
        Response response;
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
        else if(!(response = Client.getInstance().checkRegister(username.getText(), password.getText(),
                nickname.getText())).getMassage().equals(registerEnum.successfulCreate.regex)) {
            if(VBox.getChildren().size() == 8) {
                Text text = new Text();
                text.setText(response.getMassage());
                VBox.getChildren().add(text);
                VBox.getChildren().get(8).setStyle("-fx-fill: red");
            }
            else
                ((Text) VBox.getChildren().get(VBox.getChildren().size() - 1)).
                        setText(response.getMassage());
        }
        else {
            if(VBox.getChildren().size() == 9)
                VBox.getChildren().remove(VBox.getChildren().size() - 1);
            Client.getInstance().setLoggedInUser((User) response.getParams().get("user"));
            MainMenu mainMenu = new MainMenu();
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            mainMenu.start(stage);
        }

    }
}
