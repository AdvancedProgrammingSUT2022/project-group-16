import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.format.DateTimeFormatter;


public class Main extends Application
{
	public static DateTimeFormatter timeAndDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	public static AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());

	@Override
	public void start(Stage stage) throws Exception {
		chatServer server = new chatServer();
		server.update();
		WelcomePage welcomePage = new WelcomePage();
		welcomePage.start(stage);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
