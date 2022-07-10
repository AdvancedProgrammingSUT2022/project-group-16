import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

public class Main extends Application
{
	public static DateTimeFormatter timeAndDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	public static AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());
	@Override
	public void start(Stage stage) throws Exception {
		AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());
//		audioClip.play();
		WelcomePage welcomePage = new WelcomePage();
		welcomePage.start(stage);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
