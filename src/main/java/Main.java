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
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application
{
	public static Timer timer = new Timer();
	public static long timerCounter = 0;
	@Override
	public void start(Stage stage) throws Exception {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timerCounter++;
			}
		}, 1, 1000);
//		AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());
//		audioClip.play();
		WelcomePage welcomePage = new WelcomePage();
		welcomePage.start(stage);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
