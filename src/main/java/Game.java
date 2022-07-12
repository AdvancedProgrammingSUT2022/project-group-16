import Controllers.GameController;
import Models.Player.Player;
import Models.Terrain.Hex;
import Models.Terrain.Position;
import Models.Terrain.Tile;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ArrayList;

public class Game extends Application {
    private final Hex[][] hexagons = new Hex[10][10];
    private final GameController gameController = GameController.getInstance();
    ArrayList<Hex> playerTurnTiles = new ArrayList<>();
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
                hexagons[i][j] = new Hex(new Position(x, y), pane);
                y += 60;
            }
            x += 80;
        }
        gameController.initGame();
        generateMapForPlayer(gameController.getPlayerTurn());
    }
    //TODO when the turn changes delete playerTurnTiles from pane
    public void generateMapForPlayer(Player player){
        for (Tile tile : player.getMap().keySet()) {
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileState(player.getMap().get(tile));
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileType(tile.getTileType());
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileFeature(tile.getTileFeature());
            playerTurnTiles.add(hexagons[tile.getPosition().X][tile.getPosition().Y]);
        }
        playerTurnTiles.forEach(Hex::addHex);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
