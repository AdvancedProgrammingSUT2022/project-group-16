import Controllers.GameController;
import Models.Player.Civilization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;

public class NewGame extends Application {
    private final Button[] buttons = new Button[10];
    private final GameController gameController = new GameController();
    public Pane list;

    private void makeButtons() {
        for(int i = 0; i < 10; i++) {
            buttons[i] = new Button();
            buttons[i].setText(String.valueOf(Civilization.values()[i]));
            setButtonStyle(buttons[i]);
        }
    }

    private void setButtonStyle(Button button) {
        button.setStyle("-fx-pref-width: 400; -fx-pref-height: 35;" +
                "-fx-border-color: black; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 11;" +
                " -fx-text-fill: black; -fx-font-size: 20");
    }

    public void initialize() throws MalformedURLException {
        makeButtons();
        VBox vBox = new VBox();
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(540);
        rectangle.setHeight(610);
        rectangle.setX(370);
        rectangle.setY(75);
        rectangle.setStyle("-fx-fill: #002ec9");
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(String.valueOf(new URL(getClass().getResource("photos/backgrounds/icons/frontGamePage.jpg").toExternalForm()))));
        imageView.setX(375);
        imageView.setY(80);
        imageView.setFitWidth(530);
        imageView.setFitHeight(600);
        vBox.setSpacing(10);
        vBox.setLayoutX(460);
        vBox.setLayoutY(100);
        vBox.getChildren().addAll(buttons);
        list.getChildren().add(0, rectangle);
        list.getChildren().add(1, imageView);
        list.getChildren().add(vBox);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/NewGame.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
