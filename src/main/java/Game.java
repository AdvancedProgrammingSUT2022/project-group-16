import Models.Terrain.Hex;
import Models.Terrain.Position;
import Models.Terrain.TileType;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.net.URL;

public class Game extends Application {
    Hex[][] Hexagons = new Hex[10][10];
    @FXML
    private Pane pane;
    @Override

    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/game.fxml").toExternalForm()))));
        stage.show();
    }

    public void initialize(){
        int x = 50;
        for(int i = 0; i < 10; i++){
            int y = (i % 2 == 0 ? 50 : 80);
            for(int j = 0; j < 10 ; j++){
                Hexagons[i][j] = new Hex(new Position(x, y));
                pane.getChildren().add(Hexagons[i][j]);
                y += 60;
            }
            x += 80;
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
