import IO.Client;
import IO.Response;
import Models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;

public class loginPage extends Application {
    public TextField username;
    public TextField password;
    public VBox borderPane;

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

    public void checkInputs(MouseEvent mouseEvent) throws Exception {
        Response response;
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
        else if((response = Client.getInstance().checkLogin(username.getText(), password.getText())).getStatus() == 401) {
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
        else if(response.getStatus() == 402) {
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
            Client.getInstance().setLoggedInUser((User) response.getParams().get("user"));
            MainMenu mainMenu = new MainMenu();
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            mainMenu.start(stage);
        }

    }

}
