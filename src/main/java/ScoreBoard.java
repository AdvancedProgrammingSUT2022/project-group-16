import Models.Menu.Menu;
import Models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.Comparator;

public class ScoreBoard extends Application {
    public Pane list;
    private final VBox vBox = new VBox();
    private final VBox photos = new VBox();

    public void initialize() {
        photos.setAlignment(Pos.CENTER);
        vBox.setSpacing(82);
        vBox.setOnScroll((ScrollEvent event) -> {
            double yScale = 30;
            double deltaY = event.getDeltaY();
            if (deltaY < 0)
                yScale *= -1;
            if((vBox.getLayoutY() + vBox.getHeight() > 650 && yScale < 0) || vBox.getLayoutY() < 40 && yScale > 0) {
                vBox.setLayoutY(vBox.getLayoutY() + yScale);
                photos.setLayoutY(photos.getLayoutY() + yScale);
            }
        });
        photos.setSpacing(20);
        for(User user : Menu.allUsers)
            user.setScore(user.getScore() * -1);
        Menu.allUsers.sort(Comparator.comparing(User::getScore).thenComparing(User::getLastTimeOfWin).thenComparing(User::getUsername));
        for(User user : Menu.allUsers)
            user.setScore(user.getScore() * -1);
        for (int i = 0; i < Menu.allUsers.size(); i++) {
            Label label = new Label();
            User user = Menu.allUsers.get(i);
            label.setText((i + 1) + " - username: " +
                    user.getUsername() + " - Score: " +
                    user.getScore() + " - last time of winning: " +
                    user.getLastTimeOfWin() + " - last login: " + user.getLastLogin());
            if(Menu.loggedInUser == user)
                setLoggedInLabelStyle(label);
            else
                setLabelStyle(label);
            vBox.getChildren().add(label);
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(String.valueOf(user.getPhoto())));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            photos.getChildren().add(imageView);
        }
        list.getChildren().add(photos);
        list.getChildren().add(vBox);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(130);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(40);
        list.getChildren().get(list.getChildren().size() - 2).setLayoutX(1100);
        list.getChildren().get(list.getChildren().size() - 2).setLayoutY(8);
    }
    private void setLabelStyle(Label label) {
        label.setStyle("-fx-font-size: 30; " +
                "-fx-text-fill: white");
    }
    private void setLoggedInLabelStyle(Label label) {
        label.setStyle("-fx-font-size: 30; " +
                "-fx-text-fill: white;" +
                "-fx-background-color: black;" +
                "-fx-background-radius: 4;" +
                "-fx-border-color: red;" +
                "-fx-border-width: 3");
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/scoreBoard.fxml").toExternalForm()))));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void backToMainMenu(MouseEvent mouseEvent) throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow());
    }
}
