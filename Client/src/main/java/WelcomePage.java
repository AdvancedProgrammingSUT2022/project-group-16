import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
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
        Media media = new Media(getClass().getResource("trailer/trailer.mp4").toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        Pane root = FXMLLoader.load(new
                URL(getClass().getResource("fxml/welcomePage.fxml").toExternalForm()));
        root.getChildren().add(mediaView);

        stage.setScene(new Scene(root));
        stage.show();

        player.play();
        player.setOnEndOfMedia(() -> {
            root.getChildren().remove(mediaView);
            Main.audioClip.play();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
