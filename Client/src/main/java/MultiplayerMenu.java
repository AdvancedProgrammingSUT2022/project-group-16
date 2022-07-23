import IO.Client;
import IO.Request;
import IO.Response;
import Models.Player.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class MultiplayerMenu extends Application
{
	@FXML
	private Pane pane;
	@FXML
	private Group mainGroup;
	@FXML
	private Group createRoomGroup;
	@FXML
	private Group joinRoomGroup;
	@FXML
	private Group insideRoomGroup;

	@FXML
	private Label createRoomLabel;
	@FXML
	private TextField roomIDTextField;

	@FXML
	private Label findRoomLabel;
	@FXML
	private TextField findRoomRoomIDTextField;


	Socket socket;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;


	@Override
	public void start(Stage stage) throws Exception
	{
		stage.setScene(new Scene(FXMLLoader.load(new URL(getClass().getResource("fxml/multiplayerMenu.fxml").toExternalForm()))));
		stage.show();
	}
	public void initialize() throws IOException
	{
		socket = Client.socket;
		dataInputStream = new DataInputStream(socket.getInputStream());
		dataOutputStream = new DataOutputStream(socket.getOutputStream());

		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				DataInputStream listenerDIS = null;
				try
				{
					listenerDIS = new DataInputStream(Client.listenerSocket.getInputStream());
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
				String requestFromServer;
				while (true)
				{
					try
					{
						requestFromServer = listenerDIS.readUTF();
						if(requestFromServer.equals("terminate thread"))
							break;
						if(requestFromServer.equals("update inside room") || requestFromServer.startsWith("you have a new join request"))
							updateInsideRoom();
						else if(requestFromServer.equals("game started"))
							loadGame();
					}
					catch (IOException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		};
		Thread listenereThread = new Thread(runnable);
		listenereThread.setDaemon(true);
		listenereThread.start();
	}


	@FXML
	private void createRoomClicked(MouseEvent mouseEvent)
	{
		pane.getChildren().get(0).setDisable(true);
		pane.getChildren().get(0).setVisible(false);
		pane.getChildren().get(1).setDisable(false);
		pane.getChildren().get(1).setVisible(true);
	}
	@FXML
	private void joinRoomClicked(MouseEvent mouseEvent)
	{
		pane.getChildren().get(0).setDisable(true);
		pane.getChildren().get(0).setVisible(false);
		pane.getChildren().get(2).setDisable(false);
		pane.getChildren().get(2).setVisible(true);
	}
	@FXML
	private void backToMainMenuClicked(MouseEvent mouseEvent) throws Exception
	{
		MainMenu mainMenu = new MainMenu();
		mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
	}

	@FXML
	private void createRoomToServer()
	{
		String roomID = roomIDTextField.getText();
		Request request = new Request();
		request.setAction("new room");
		request.addParam("roomID", roomID);

		try
		{
			dataOutputStream.writeUTF(request.toJson());
			dataOutputStream.flush();

			Response response = Response.fromJson(dataInputStream.readUTF());
			createRoomResultPanel(response.getMassage(), !response.getMassage().equals("this roomID is already taken"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	@FXML
	private void joinRoomToServer()
	{
		String roomID = findRoomRoomIDTextField.getText();
		Request request = new Request();
		request.setAction("join room");
		request.addParam("roomID", roomID);

		try
		{
			dataOutputStream.writeUTF(request.toJson());
			dataOutputStream.flush();

			Response response = Response.fromJson(dataInputStream.readUTF());
			createRoomResultPanel(response.getMassage(), false);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@FXML
	private void updateInsideRoom()
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				// set joined clients
				insideRoomGroup.getChildren().clear();
				Request request = new Request();
				request.setAction("get joined clients");
				try
				{
					dataOutputStream.writeUTF(request.toJson());
					dataOutputStream.flush();
					Response response = Response.fromJson(dataInputStream.readUTF());
					System.out.println(response.getParams().get("joinedClients"));
					ArrayList<String> joinedClients = (ArrayList<String>) response.getParams().get("joinedClients");

					Label titleLabel = new Label();
					titleLabel.setLayoutX(150);
					titleLabel.setLayoutY(150);
					titleLabel.setText("players in this room");
					insideRoomGroup.getChildren().add(titleLabel);
					for (int i = 0; i < joinedClients.size(); i++)
					{
						String joinedClient = joinedClients.get(i);

						Label label = new Label();
						label.setText(joinedClient);
						label.setLayoutX(150);
						label.setLayoutY(200 + i * 50);

						insideRoomGroup.getChildren().add(label);
					}
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}

				// set joined requests
				request = new Request();
				request.setAction("get join requests");
				try
				{
					dataOutputStream.writeUTF(request.toJson());
					Response response = Response.fromJson(dataInputStream.readUTF());
					ArrayList<String> joinRequests = (ArrayList<String>) response.getParams().get("joinRequests");

					Label titleLabel = new Label();
					titleLabel.setLayoutX(500);
					titleLabel.setLayoutY(150);
					titleLabel.setText("join requests:");
					insideRoomGroup.getChildren().add(titleLabel);
					for (int i = 0; i < joinRequests.size(); i++)
					{
						String joinedClient = joinRequests.get(i);
						final int index = i;
						Button acceptButton = new Button();
						Button rejectButton = new Button();
						acceptButton.setOnMouseClicked((click) -> {acceptJoinRequest(index);});
						acceptButton.setLayoutX(500);
						acceptButton.setLayoutY(200 + i * 50);
						acceptButton.setText("accept");
						rejectButton.setOnMouseClicked((click) -> {rejectJoinRequest(index);});
						rejectButton.setLayoutX(570);
						rejectButton.setLayoutY(200 + i * 50);
						rejectButton.setText("reject");

						Label label = new Label();
						label.setText(joinedClient);
						label.setLayoutX(630);
						label.setLayoutY(200 + i * 50);

						insideRoomGroup.getChildren().add(acceptButton);
						insideRoomGroup.getChildren().add(rejectButton);
						insideRoomGroup.getChildren().add(label);
					}
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}

				Button startGameButton = new Button();
				startGameButton.setLayoutX(150);
				startGameButton.setLayoutY(600);
				startGameButton.setText("Start");
				startGameButton.getStyleClass().add("icons");
				startGameButton.setOnMouseClicked((click) -> {startGame();});
				insideRoomGroup.getChildren().add(startGameButton);
			}
		};

		Platform.runLater(runnable);
	}

	private void acceptJoinRequest(int index)
	{
		Request request = new Request();
		request.setAction("accept join request");
		request.addParam("index", String.valueOf(index));
		try
		{
			dataOutputStream.writeUTF(request.toJson());
			dataOutputStream.flush();
			dataInputStream.readUTF();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		updateInsideRoom();
	}
	private void rejectJoinRequest(int index)
	{
		Request request = new Request();
		request.setAction("reject join request");
		request.addParam("index", String.valueOf(index));
		try
		{
			dataOutputStream.writeUTF(request.toJson());
			dataOutputStream.flush();
			dataInputStream.readUTF();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		updateInsideRoom();
	}

	private void startGame()
	{
		Request request = new Request();
		request.setAction("start game");
		try
		{
			dataOutputStream.writeUTF(request.toJson());
			dataOutputStream.flush();
			dataInputStream.readUTF();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void loadGame()
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				Request request = new Request();
				request.setAction("get player");
				Player player;
				try
				{
					dataOutputStream.writeUTF(request.toJson());
					dataOutputStream.flush();
					Response response = Response.fromJson(dataInputStream.readUTF());
					player = (Player) response.getParams().get("player");
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}

				Game game = new Game();
				Main.audioClip.stop();
				try
				{
					game.start((Stage) pane.getScene().getWindow());
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		Platform.runLater(runnable);
	}


	private void createRoomResultPanel(String text, boolean isSuccessful) {
		//define box
		VBox box = new VBox();
		addLabelToBox(text, box);
		boxStyle(box);
		pane.getChildren().add(box);
		setCoordinates(pane, 730, 330);

		//disable other Nodes
		for (int i = 0; i < pane.getChildren().size() - 1; i++)
			pane.getChildren().get(i).setDisable(true);

		//click on error box and make other Nodes available
		box.setOnMouseClicked(mouseEvent -> {
			pane.getChildren().remove(pane.getChildren().size() - 1);
			for (int i = 0; i < pane.getChildren().size(); i++)
				pane.getChildren().get(i).setDisable(false);
			if (isSuccessful) {
				pane.getChildren().get(1).setDisable(true);
				pane.getChildren().get(1).setVisible(false);
				pane.getChildren().get(3).setDisable(false);
				pane.getChildren().get(3).setVisible(true);
				updateInsideRoom();
			}
		});
	}

	//this method adds a line to Vbox
	private void addLabelToBox(String line, VBox box) {
		Label label = new Label();
		label.setText(line);
		labelStyle(label);
		box.getChildren().add(label);
	}

	//style methods
	private void boxStyle(VBox box) {
		box.setSpacing(5);
		box.setAlignment(Pos.CENTER);
	}
	private void labelStyle(Label label) {
		label.setStyle("-fx-text-fill: white;" +
				"-fx-font-size: 18;");
	}

	//this method set the last node of pane to (x,y)
	private void setCoordinates(Pane box, double x, double y) {
		box.getChildren().get(box.getChildren().size() - 1).setLayoutX(x);
		box.getChildren().get(box.getChildren().size() - 1).setLayoutY(y);
	}
}
















