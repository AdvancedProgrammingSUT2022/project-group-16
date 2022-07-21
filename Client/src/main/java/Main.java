import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.format.DateTimeFormatter;


public class Main extends Application
{
	static final int SERVER_PORT = 444;
	static DataInputStream dataInputStream;
	static DataOutputStream dataOutputStream;
	public static DateTimeFormatter timeAndDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	public static AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());

	@Override
	public void start(Stage stage) throws Exception {
		Socket socket = new Socket("localhost",SERVER_PORT);
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		dataInputStream = new DataInputStream(socket.getInputStream());

		chatServer server = new chatServer();
		server.update();
		AudioClip audioClip = new AudioClip(Main.class.getResource("audio/1.mp3").toExternalForm());
		audioClip.play();
		WelcomePage welcomePage = new WelcomePage();
		welcomePage.start(stage);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
