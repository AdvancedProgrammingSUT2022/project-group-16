package IO;

import Controllers.RegisterController;
import IO.RequestHandler;
import server.chatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;


public class Server
{
	static final int SERVER_PORT = 1111;
	static ServerSocket serverSocket;
	public static chatServer chatServer = new chatServer();
	public static RegisterController registerController = new RegisterController();
	public static DateTimeFormatter timeAndDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	public static void main(String[] args) {
		chatServer.update();
		registerController.updateDatabase();
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			try {
				Socket socket = serverSocket.accept();
				RequestHandler handler = new RequestHandler(socket);
				handler.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
