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
	private boolean isMakingRoomPrivate = false;
	private int capacity;

	@FXML
	public TextField capacityTextField;
	@FXML
	public Button roomPrivateButton;
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
	public void privateRoom(MouseEvent mouseEvent) {
		changePrivateButtonStyle();
		isMakingRoomPrivate = !isMakingRoomPrivate;
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
		this.capacity = Integer.parseInt(capacityTextField.getText());

		String roomID = roomIDTextField.getText();
		String capacity = capacityTextField.getText();

		Request request = new Request();
		request.setAction("new room");
		request.addParam("roomID", roomID);
		request.addParam("private", isMakingRoomPrivate);
		request.addParam("capacity", capacity);

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
	public void findRoomToServer(MouseEvent mouseEvent) {
		Request request = new Request();
		request.setAction("find room");

		try {
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

				ArrayList<String> joinedClientsUsernames;
				ArrayList<String> joinedClientsNicknames;
				try
				{
					dataOutputStream.writeUTF(request.toJson());
					dataOutputStream.flush();
					Response response = Response.fromJson(dataInputStream.readUTF());

					System.out.println(response.getParams().get("joinedClients"));

					joinedClientsUsernames = (ArrayList<String>) response.getParams().get("joinedClientsUsernames");
					joinedClientsNicknames = (ArrayList<String>) response.getParams().get("joinedClientsNicknames");

					playersInRoomBox(joinedClientsUsernames, joinedClientsNicknames);
					insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 1).setLayoutX(150);
					insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 1).setLayoutY(150);

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

					requestsInRoomBox(joinRequests);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}

				Label acceptedClients = new Label();
				acceptedClients.setText((joinedClientsNicknames.size() + 1)+ " / " + capacity);
				namesStyle(acceptedClients);
				acceptedClients.setLayoutX(300);
				acceptedClients.setLayoutY(110);
				insideRoomGroup.getChildren().add(acceptedClients);

				Label groupType = new Label();
				if (isMakingRoomPrivate) groupType.setText("type: private");
				else groupType.setText("type: public");
				namesStyle(groupType);
				groupType.setLayoutX(520);
				groupType.setLayoutY(110);
				insideRoomGroup.getChildren().add(groupType);

				Button startGameButton = new Button();
				startGameButton.setLayoutX(150);
				startGameButton.setLayoutY(600);
				startGameButton.setText("Start");
				startGameButton.getStyleClass().add("icons");
				startGameButton.setOnMouseClicked((click) -> {startGame();});
				insideRoomGroup.getChildren().add(startGameButton);

				if (joinedClientsNicknames.size() == capacity - 1)
					for (int i = 0; i < insideRoomGroup.getChildren().size() - 1; i++)
						insideRoomGroup.getChildren().get(i).setDisable(true);
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


	//panels
	private void createRoomResultPanel(String text, boolean isSuccessful) {
		//define box
		VBox box = new VBox();
		Label label = new Label();
		titleStyle(label);
		label.setText(text);
		box.getChildren().add(label);
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
	private void playersInRoomBox(ArrayList<String> joinedClientsUsernames, ArrayList<String> joinedClientsNicknames) {
		VBox box = new VBox();
		boxStyle(box);

		Label label = new Label();
		titleStyle(label);
		label.setText("players in this room:");
		box.getChildren().add(label);
		box.getChildren().get(box.getChildren().size() - 1).setLayoutX(150);
		box.getChildren().get(box.getChildren().size() - 1).setLayoutY(150);

		for (int i = 0; i < joinedClientsUsernames.size(); i++) {
			Label name = new Label();
			acceptedNamesStyle(name);
			name.setText("U: " + joinedClientsUsernames.get(i) + " - N: " + joinedClientsNicknames.get(i));
			box.getChildren().add(name);
		}

		insideRoomGroup.getChildren().add(box);
	}
	private void requestsInRoomBox(ArrayList<String> joinRequests) {
		VBox names = new VBox(), acceptButtons = new VBox(), rejectButtons = new VBox();
		boxStyle(names);
		boxStyle(acceptButtons);
		boxStyle(rejectButtons);

		Label label = new Label();
		titleStyle(label);
		label.setText("join requests:");
		insideRoomGroup.getChildren().add(label);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 1).setLayoutX(500);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 1).setLayoutY(150);

		for (int i = 0; i < joinRequests.size(); i++)
		{
			String joinedClient = joinRequests.get(i);
			final int index = i;
			Button acceptButton = new Button();
			Button rejectButton = new Button();

			acceptButton.setOnMouseClicked((click) -> acceptJoinRequest(index));
			acceptButton.setText("accept");
			buttonStyle(acceptButton);
			acceptButtons.getChildren().add(acceptButton);

			rejectButton.setOnMouseClicked((click) -> rejectJoinRequest(index));
			rejectButton.setText("reject");
			buttonStyle(rejectButton);
			rejectButtons.getChildren().add(rejectButton);

			Label name = new Label();
			namesStyle(name);
			name.setText(joinedClient);
			names.getChildren().add(name);

		}
		insideRoomGroup.getChildren().addAll(names, acceptButtons, rejectButtons);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 3).setLayoutX(510);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 3).setLayoutY(230);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 2).setLayoutX(605);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 2).setLayoutY(230);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 1).setLayoutX(700);
		insideRoomGroup.getChildren().get(insideRoomGroup.getChildren().size() - 1).setLayoutY(230);

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
	private void buttonStyle(Button button) {
		button.setStyle("-fx-background-color: #ff7300;" +
				"-fx-border-color: white;" +
				"-fx-border-radius: 4;" +
				"-fx-background-radius: 7;" +
				"-fx-border-width: 3;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 15;" +
				"-fx-pref-width: 85");
		button.setOnMouseMoved(mouseEvent -> button.setStyle("-fx-background-color: #8c3f00;" +
				"-fx-border-color: white;" +
				"-fx-border-radius: 4;" +
				"-fx-background-radius: 7;" +
				"-fx-border-width: 3;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 15;" +
				"-fx-pref-width: 85"));
		button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-background-color: #ff7300;" +
				"-fx-border-color: white;" +
				"-fx-border-radius: 4;" +
				"-fx-background-radius: 7;" +
				"-fx-border-width: 3;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 15;" +
				"-fx-pref-width: 85"));
	}
	private void changePrivateButtonStyle() {
		if (isMakingRoomPrivate)
			roomPrivateButton.setStyle("-fx-background-color: #ff0000;" +
					"-fx-border-color: white;" +
					"-fx-border-radius: 4;" +
					"-fx-background-radius: 7;" +
					"-fx-border-width: 3;" +
					"-fx-text-fill: white;" +
					"-fx-font-size: 15;" +
					"-fx-pref-width: 85");
		else
			roomPrivateButton.setStyle("-fx-background-color: #09ff00;" +
					"-fx-border-color: white;" +
					"-fx-border-radius: 4;" +
					"-fx-background-radius: 7;" +
					"-fx-border-width: 3;" +
					"-fx-text-fill: #000000;" +
					"-fx-font-size: 15;" +
					"-fx-pref-width: 85");
	}
	private void namesStyle(Label label) {
		label.setStyle("-fx-background-color: #ff7300;" +
				"-fx-border-color: white;" +
				"-fx-border-radius: 4;" +
				"-fx-background-radius: 7;" +
				"-fx-border-width: 3;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 15;" +
				"-fx-pref-width: 85;" +
				"-fx-alignment: center;" +
				"-fx-pref-height: 35");
	}
	private void acceptedNamesStyle(Label label) {
		label.setStyle("-fx-background-color: #ff7300;" +
				"    -fx-text-fill: #690404;" +
				"    -fx-font-size: 30;" +
				"    -fx-border-width: 5;" +
				"    -fx-border-color: black;" +
				"    -fx-pref-width: 300;" +
				"    -fx-alignment: center;" +
				"    -fx-pref-height: 70;" +
				"    -fx-border-radius: 5;" +
				"    -fx-background-radius: 8;");
	}
	private void titleStyle(Label label) {
		label.setStyle("-fx-background-color: #ff7300;" +
				"    -fx-text-fill: #690404;" +
				"    -fx-font-size: 20;" +
				"    -fx-border-width: 5;" +
				"    -fx-border-color: black;" +
				"    -fx-pref-width: 300;" +
				"    -fx-alignment: center;" +
				"    -fx-pref-height: 70;" +
				"    -fx-border-radius: 5;" +
				"    -fx-background-radius: 8;");
	}


	//this method set the last node of pane to (x,y)
	private void setCoordinates(Pane box, double x, double y) {
		box.getChildren().get(box.getChildren().size() - 1).setLayoutX(x);
		box.getChildren().get(box.getChildren().size() - 1).setLayoutY(y);
	}
}
















