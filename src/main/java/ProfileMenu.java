import Controllers.ProfileController;
import enums.profileEnum;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;

public class ProfileMenu extends Application {
    public Pane list;

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

    public void changeNicknameMenu(MouseEvent mouseEvent) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.getChildren().add(new Label("enter your new nickname: "));
        vBox.getChildren().get(0).setStyle("-fx-font-size: 25; -fx-text-fill: white");
        vBox.getChildren().add(new TextField());
        vBox.getChildren().get(1).setStyle("-fx-max-width: 300; -fx-pref-height: 30; -fx-background-color: #2181ff; -fx-text-fill: white");
        vBox.setStyle("-fx-background-color: rgba(0,76,107,0.57); -fx-pref-height: 250; " +
                "-fx-pref-width: 500; -fx-background-radius: 100; -fx-border-width: 10; " +
                "-fx-border-color: #000088; -fx-border-radius: 100");
        vBox.getChildren().add(new Button());
        vBox.getChildren().add(new Button());
        ((Button) vBox.getChildren().get(2)).setText("done");
        ((Button) vBox.getChildren().get(3)).setText("back");
        vBox.getChildren().get(2).setStyle("-fx-pref-width: 200; -fx-pref-height: 25;");
        vBox.getChildren().get(3).setStyle("-fx-pref-width: 150; -fx-pref-height: 18;");
        ((Button) vBox.getChildren().get(2)).setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                String message = checkNewNickname(((TextField) vBox.getChildren().get(1)).getText());
                if(message.equals(profileEnum.successfulNicknameChange.regex)) {
                    list.getChildren().remove(list.getChildren().size() - 1);
                    list.getChildren().get(0).setDisable(false);
                }
                else if(vBox.getChildren().size() == 4) {
                    vBox.getChildren().add(new Text(message));
                    vBox.getChildren().get(4).setStyle("-fx-fill: #ffffff");
                }
                else
                    ((Text) vBox.getChildren().get(vBox.getChildren().size() - 1)).setText(message);
            }
        });
        ((Button) vBox.getChildren().get(3)).setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                list.getChildren().remove(list.getChildren().size() - 1);
                list.getChildren().get(0).setDisable(false);
            }
        });
        list.getChildren().add(vBox);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(390);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(200);
        list.getChildren().get(0).setDisable(true);
    }

    public void changePasswordMenu(MouseEvent mouseEvent) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.getChildren().add(new Label("enter your current password: "));
        vBox.getChildren().get(0).setStyle("-fx-font-size: 25; -fx-text-fill: white");
        vBox.getChildren().add(new TextField());
        vBox.getChildren().add(new Label("enter your new password: "));
        vBox.getChildren().get(2).setStyle("-fx-font-size: 25; -fx-text-fill: white");
        vBox.getChildren().add(new TextField());
        vBox.getChildren().get(1).setStyle("-fx-max-width: 300; -fx-pref-height: 30; -fx-background-color: #2181ff; -fx-text-fill: white");
        vBox.setStyle("-fx-background-color: rgba(0,76,107,0.57); -fx-pref-height: 250; " +
                "-fx-pref-width: 500; -fx-background-radius: 100; -fx-border-width: 10; " +
                "-fx-border-color: #000088; -fx-border-radius: 100");
        vBox.getChildren().get(3).setStyle("-fx-max-width: 300; -fx-pref-height: 30; -fx-background-color: #2181ff; -fx-text-fill: white");
        vBox.setStyle("-fx-background-color: rgba(0,76,107,0.57); -fx-pref-height: 400; " +
                "-fx-pref-width: 500; -fx-background-radius: 100; -fx-border-width: 10; " +
                "-fx-border-color: #000088; -fx-border-radius: 100");
        vBox.getChildren().add(new Button());
        vBox.getChildren().add(new Button());
        ((Button) vBox.getChildren().get(4)).setText("done");
        ((Button) vBox.getChildren().get(5)).setText("back");
        vBox.getChildren().get(4).setStyle("-fx-pref-width: 200; -fx-pref-height: 25;");
        vBox.getChildren().get(5).setStyle("-fx-pref-width: 150; -fx-pref-height: 18;");
        ((Button) vBox.getChildren().get(4)).setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                String message = checkNewPassword(((TextField) vBox.getChildren().get(1)).getText(), ((TextField) vBox.getChildren().get(3)).getText());
                if(message.equals(profileEnum.successfulPassChange.regex)) {
                    list.getChildren().remove(list.getChildren().size() - 1);
                    list.getChildren().get(0).setDisable(false);
                }
                else if(vBox.getChildren().size() == 6) {
                    vBox.getChildren().add(new Text(message));
                    vBox.getChildren().get(6).setStyle("-fx-fill: #ffffff");
                }
                else
                    ((Text) vBox.getChildren().get(vBox.getChildren().size() - 1)).setText(message);
            }
        });
        ((Button) vBox.getChildren().get(5)).setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                list.getChildren().remove(list.getChildren().size() - 1);
                list.getChildren().get(0).setDisable(false);
            }
        });
        list.getChildren().add(vBox);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(390);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(200);
        list.getChildren().get(0).setDisable(true);
    }

    private String checkNewNickname(String nickname) {
        ProfileController profileController = new ProfileController();
        return profileController.changeNickname(nickname);
    }
    private String checkNewPassword(String currPass, String newPass) {
        ProfileController profileController = new ProfileController();
        return profileController.matchNewPassword(currPass, newPass);
    }
}
