import Models.Menu.Menu;
import Models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Comparator;

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

    public void exit(MouseEvent mouseEvent) {
        Platform.exit();
    }

    public void logout(MouseEvent mouseEvent) throws Exception {
        Menu.loggedInUser = null;
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
    private void setVboxStyle(VBox vBox) {
        vBox.setLayoutX(700);
        vBox.setLayoutY(100);
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: rgb(255,166,0); " +
                "-fx-font-size: 25;" +
                "-fx-border-color: black; " +
                "-fx-border-width: 7; " +
                "-fx-border-radius: 100; " +
                "-fx-background-radius: 110;" +
                "-fx-pref-width: 450;" +
                "-fx-pref-height: 550");
    }
    private void setButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #ff7300;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #111111;" +
                "-fx-border-width: 4;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8");
    }
    private void setHoverButtonStyle(Button button) {
        button.setStyle("-fx-background-color: rgb(255,47,0);" +
                "-fx-text-fill: white;" +
                "-fx-border-color: #111111;" +
                "-fx-border-width: 4;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8");
    }
    public void disableMenu(boolean e) {
        list.getChildren().get(0).setDisable(e);
        list.getChildren().get(1).setDisable(e);
        list.getChildren().get(2).setDisable(e);
        list.getChildren().get(3).setDisable(e);
        list.getChildren().get(4).setDisable(e);
        list.getChildren().get(5).setDisable(e);
    }

}
