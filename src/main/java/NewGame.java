import Controllers.GameController;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.User;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import Models.Menu.Menu;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class NewGame extends Application {
    private final ArrayList<Button> buttons = new ArrayList<>();
    private final GameController gameController = GameController.getInstance();
    public Pane list;
    private User user = Menu.loggedInUser;

    private void makeButtons() {
        for(int i = 0; i < 10; i++) {
            buttons.add(new Button());
            buttons.get(i).setText(Civilization.values()[i] + " - leader: " + Civilization.values()[i].leaderName);
            setButtonStyle(buttons.get(i));
        }
    }

    private void setChooseButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #2525fd;" +
                "-fx-text-fill: white;" +
                "-fx-pref-width: 150;" +
                "-fx-max-height: 35;" +
                "-fx-font-size: 20;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 6; ");
    }
    private void setHoverChooseButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #0000a6;" +
                "-fx-text-fill: white;" +
                "-fx-pref-width: 150;" +
                "-fx-max-height: 35;" +
                "-fx-font-size: 20;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 6; ");
    }
    private void setGoButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #2525fd;" +
                "-fx-text-fill: white;" +
                "-fx-pref-width: 100;" +
                "-fx-max-height: 35;" +
                "-fx-font-size: 20;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 6; ");
    }
    private void setHoverGoButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #0000a6;" +
                "-fx-text-fill: white;" +
                "-fx-pref-width: 100;" +
                "-fx-max-height: 35;" +
                "-fx-font-size: 20;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 6; ");
    }
    private void setButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #2525fd; " +
                "-fx-pref-width: 400; " +
                "-fx-pref-height: 35;" +
                "-fx-border-color: black; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 11;" +
                " -fx-text-fill: white; " +
                "-fx-font-size: 20; ");
    }
    private void setHoverButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #0000a6; " +
                "-fx-pref-width: 400; " +
                "-fx-pref-height: 35;" +
                "-fx-border-color: black; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 11;" +
                " -fx-text-fill: white; " +
                "-fx-font-size: 20; ");
    }

    public void initialize() throws MalformedURLException {
        makeButtons();
        VBox vBox = new VBox();
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(540);
        rectangle.setHeight(610);
        rectangle.setX(370);
        rectangle.setY(75);
        rectangle.setStyle("-fx-fill: #002ec9");
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(String.valueOf(new URL(Objects.requireNonNull(getClass().
                getResource("photos/backgrounds/icons/frontGamePage.jpg")).toExternalForm()))));
        imageView.setX(375);
        imageView.setY(80);
        imageView.setFitWidth(530);
        imageView.setFitHeight(600);
        vBox.setSpacing(10);
        vBox.setLayoutX(460);
        vBox.setLayoutY(100);
        Button choosePlayer = new Button();
        choosePlayer.setText("next Player");
        choosePlayer.setLayoutX(100);
        choosePlayer.setLayoutY(250);
        setChooseButtonStyle(choosePlayer);
        vBox.getChildren().addAll(buttons);
        list.getChildren().add(0, rectangle);
        list.getChildren().add(1, imageView);
        ((Text) list.getChildren().get(5)).setText(Menu.loggedInUser.getUsername());
        list.getChildren().add(vBox);
        VBox newPlayers = newPlayerChoose();
        for(Button button : buttons)
        {
            button.setOnMouseMoved(mouseEvent -> {
                setHoverButtonStyle(button);
            });
            button.setOnMouseExited(mouseEvent -> {
                setButtonStyle(button);
            });
            button.setOnMousePressed(mouseEvent -> {
                list.getChildren().get(list.getChildren().size() - 2).setDisable(false);
                if(((Text) list.getChildren().get(4)).getText().equals("4")) {
                    Game game = new Game();
                    try {
                        game.start((Stage) list.getScene().getWindow());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(((Text) list.getChildren().get(4)).getText().equals("2")) {
                    Button start = new Button();
                    start.setText("go");
                    setGoButtonStyle(start);
                    start.setLayoutX(1100);
                    start.setLayoutY(640);
                    start.setOnMouseMoved(mouseEvent131 -> {
                        if(start.getStyle().charAt(23) != '0')
                            setHoverGoButtonStyle(start);
                    });
                    start.setOnMouseExited(mouseEvent131 -> {
                        if(start.getStyle().charAt(23) == '0')
                            setGoButtonStyle(start);
                    });
                    start.setOnMousePressed(mouseEvent131 -> {
                        Game game = new Game();
                        try {
                            game.start((Stage) list.getScene().getWindow());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    list.getChildren().add(list.getChildren().size() - 1, start);
                }
                button.setDisable(true);
                list.getChildren().get(list.getChildren().size() - 1).setDisable(true);
                gameController.addPlayer(new Player(Civilization.values()[buttons.indexOf(button)], user.getUsername(),
                        user.getNickname(), user.getPassword(), user.getScore()));
                list.getChildren().add(choosePlayer);
                choosePlayer.setOnMouseMoved(mouseEvent1 -> setHoverChooseButtonStyle(choosePlayer));
                choosePlayer.setOnMouseExited(mouseEvent1 -> setChooseButtonStyle(choosePlayer));
                choosePlayer.setOnMousePressed(mouseEvent14 -> {
                    list.getChildren().get(list.getChildren().size() - 3).setDisable(true);
                    list.getChildren().remove(choosePlayer);
                    list.getChildren().add(newPlayers);
                    for(int i = 1; i < Menu.allUsers.size() + 1; i++) {
                        Button tmp = (Button) ((VBox) list.getChildren().get(list.getChildren().size() - 1)).
                                getChildren().get(i);
                        int flag = i;
                        tmp.setOnMouseMoved(mouseEvent1 -> {
                            if (tmp.getStyle().charAt(23) != '0')
                                setHoverButtonStyle(tmp);
                        });
                        tmp.setOnMouseExited(mouseEvent12 -> {
                            if (tmp.getStyle().charAt(23) == '0')
                                setButtonStyle(tmp);
                        });
                        tmp.setOnMousePressed(mouseEvent13 -> {
                            user = Menu.allUsers.get(flag - 1);
                            tmp.setDisable(true);
                            list.getChildren().remove(list.getChildren().size() - 1);
                            list.getChildren().get(list.getChildren().size() - 1).setDisable(false);
                            ((Text) list.getChildren().get(5)).setText(user.getUsername());
                            ((Text) list.getChildren().get(4)).setText(String.valueOf(1 +
                                    Integer.parseInt(((Text) list.getChildren().get(4)).getText())));
                        });
                    }
                });
            });
        }
    }
    private void setVboxStyle(VBox vBox) {
        vBox.setSpacing(5);
        vBox.setLayoutX(340);
        vBox.setLayoutY(30);
        vBox.setStyle("-fx-background-color: rgba(0,166,255,0.73); -fx-border-radius: 100; " +
                "-fx-background-radius: 100; -fx-border-color: black; -fx-border-width: 3");
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefHeight(690);
        vBox.setPrefWidth(600);
    }
    private VBox newPlayerChoose() {
        VBox vBox = new VBox();
        setVboxStyle(vBox);
        ObservableList<Node> nodes = vBox.getChildren();
        nodes.add(new Label());
        ((Label) nodes.get(0)).setText("please pick next player");
        for(User user : Menu.allUsers)
        {
            nodes.add(new Button());
            ((Button) nodes.get(nodes.size() - 1)).setText(user.getUsername());
            setButtonStyle(((Button) nodes.get(nodes.size() - 1)));
            if(user == Menu.loggedInUser)
                nodes.get(nodes.size() - 1).setDisable(true);
        }
        return vBox;
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new URL(Objects.requireNonNull(getClass().
                getResource("fxml/newGame.fxml")).toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void backToMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
