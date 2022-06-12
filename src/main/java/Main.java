import Controllers.RegisterController;
import Models.Menu.Menu;
import Models.User;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application
{
	@Override
	public void start(Stage stage) throws Exception {
		AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());
		audioClip.play();
		WelcomePage welcomePage = new WelcomePage();
		welcomePage.start(stage);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
