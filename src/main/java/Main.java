import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;


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
