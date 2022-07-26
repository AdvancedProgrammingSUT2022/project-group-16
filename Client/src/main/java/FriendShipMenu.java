import IO.Client;
import IO.Request;
import IO.Response;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class FriendShipMenu extends Application {
    @FXML
    public AnchorPane pane;
    public Group friendRequests;
    public Group searchPlayer;
    public VBox friendRequestBox;
    public TextField searchTextField;
    public VBox searchNameFounded;
    public Pane friendsNamesBox;
    public Group friendsName;


    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;


    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/friendShipMenu.fxml").toExternalForm()))));
        stage.show();
    }
    public void backToProfileMenu(MouseEvent mouseEvent) throws Exception {
        ProfileMenu profileMenu = new ProfileMenu();
        profileMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }

    public void initialize() throws IOException {
        socket = Client.socket;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        //styles
        friendRequestBox.setSpacing(20);

        //request
        Request request = new Request();
        request.setAction("friend requests");
        try {
            dataOutputStream.writeUTF(request.toJson());
            dataOutputStream.flush();

            Response response = Response.fromJson(dataInputStream.readUTF());
            ArrayList<String> names = (ArrayList<String>) response.getParams().get("usernames");

            VBox yesButtons = new VBox();
            VBox noButtons = new VBox();
            yesButtons.setSpacing(2);
            noButtons.setSpacing(2);

            for (String name : names) {
                Label labelName = new Label();
                labelStyle(labelName);
                labelName.setText(name);
                friendRequestBox.getChildren().add(labelName);

                Button yes = new Button();
                yes.getStyleClass().clear();
                yes.getStyleClass().add("yesButton");
                yes.setText("yes");

                Button no = new Button();
                no.getStyleClass().clear();
                no.getStyleClass().add("noButton");
                no.setText("no");
                yesButtons.getChildren().add(yes);
                noButtons.getChildren().add(no);

                //yes pressed
                yes.setOnMouseClicked(mouseEvent -> {
                    request.setAction("accept friend");
                    request.getParams().put("username", name);
                    try
                    {
                        dataOutputStream.writeUTF(request.toJson());
                        dataOutputStream.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    yesButtons.getChildren().remove(yes);
                    noButtons.getChildren().remove(no);
                    friendRequestBox.getChildren().remove(labelName);
                    pane.getChildren().remove(friendRequestBox);
                    pane.getChildren().add(friendRequestBox);
                    pane.getChildren().remove(yesButtons);
                    pane.getChildren().add(yesButtons);
                    pane.getChildren().remove(noButtons);
                    pane.getChildren().add(noButtons);
                });

                //no pressed
                no.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        request.setAction("reject friend");
                        request.getParams().put("username", name);
                        try
                        {
                            dataOutputStream.writeUTF(request.toJson());
                            dataOutputStream.flush();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        yesButtons.getChildren().remove(yes);
                        noButtons.getChildren().remove(no);
                        friendRequestBox.getChildren().remove(labelName);
                        pane.getChildren().remove(friendRequestBox);
                        pane.getChildren().add(friendRequestBox);
                        pane.getChildren().remove(yesButtons);
                        pane.getChildren().add(yesButtons);
                        pane.getChildren().remove(noButtons);
                        pane.getChildren().add(noButtons);

                    }
                });
            }
            pane.getChildren().add(yesButtons);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutX(880);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutY(360);
            pane.getChildren().add(noButtons);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutX(1000);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutY(360);

            //friends panel
            friendsNamesPanel();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void updateFriendSearchBox() {
        String searchedField = searchTextField.getText();

        Request request = new Request();
        request.setAction("search username");
        request.getParams().put("username", searchedField);

        try {
            dataOutputStream.writeUTF(request.toJson());
            dataOutputStream.flush();

            Response response = Response.fromJson(dataInputStream.readUTF());
            ArrayList<String> names = (ArrayList<String>) response.getParams().get("usernames");
            ArrayList<String> scores = (ArrayList<String>) response.getParams().get("scores");

            searchNameFounded.getChildren().clear();

            for (int i = 0; i < names.size(); i++)
            {
                String name = names.get(i);
                String score = scores.get(i);
                addLabelToBox("name: " + name + " - score: " + score, searchNameFounded);
                searchNameFounded.getChildren().get(searchNameFounded.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                    Request sendFriendRequest = new Request();
                    sendFriendRequest.setAction("send friendShip");
                    sendFriendRequest.getParams().put("username", name);

                    try {
                        dataOutputStream.writeUTF(sendFriendRequest.toJson());
                        dataOutputStream.flush();

                        Response response1 = Response.fromJson(dataInputStream.readUTF());

                        Label label = new Label();
                        labelStyle2(label);
                        label.setText(response1.getMassage());

                        if (searchPlayer.getChildren().get(searchPlayer.getChildren().size() - 1).getClass().equals(Label.class))
                            searchPlayer.getChildren().remove(searchPlayer.getChildren().size() - 1);

                        searchPlayer.getChildren().add(label);
                        searchPlayer.getChildren().get(searchPlayer.getChildren().size() - 1).setLayoutX(275);
                        searchPlayer.getChildren().get(searchPlayer.getChildren().size() - 1).setLayoutY(595);

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //panels
    private void friendsNamesPanel() {
        Request request = new Request();
        request.setAction("get all friends");

        try {
            dataOutputStream.writeUTF(request.toJson());
            dataOutputStream.flush();

            Response response = Response.fromJson(dataInputStream.readUTF());
            ArrayList<String> friends = (ArrayList<String>) response.getParams().get("friends");

            System.out.println(friends);
            int counter = 0;
            while (counter < friends.size()) {
                VBox box = new VBox();
                addLabelToBox(friends.get(counter), box);
                counter++;
                if (counter < friends.size())
                    addLabelToBox(friends.get(counter), box);
                counter++;
                if (counter < friends.size())
                    addLabelToBox(friends.get(counter), box);
                counter++;
                friendsNamesBox.getChildren().add(box);
                friendsNamesBox.getChildren().get(friendsNamesBox.getChildren().size() - 1).setLayoutX(150 * ((counter - 1) / 2.0));
            }
            pane.getChildren().remove(friendsNamesBox);
            pane.getChildren().add(friendsNamesBox);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //this method adds a line to Vbox
    private void addLabelToBox(String line, VBox box) {
        Label label = new Label();
        label.setText(line);
        labelStyle(label);
        box.getChildren().add(label);
    }
    private void labelStyle(Label label) {
        label.setStyle("-fx-text-fill: #000000;" +
                "-fx-font-size: 18;");
    }
    private void labelStyle2(Label label) {
        label.setStyle("-fx-text-fill: #ff0000;" +
                "-fx-font-size: 18;");
    }
}
