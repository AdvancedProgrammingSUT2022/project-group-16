package Models.Terrain;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import javafx.scene.shape.Rectangle;

public class Hex extends Rectangle {
    private Position position;
    private TileType tileType;
    public Hex(Position position){
        super(position.X, position.Y, 90, 50);
        this.position = position;
        this.setBackground("/photos/Tiles/Hexagon.png");
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    private void setBackground(String url) {
        this.setFill(new ImagePattern(new Image(this.getClass().getResource(url).toExternalForm())));
    }


}
