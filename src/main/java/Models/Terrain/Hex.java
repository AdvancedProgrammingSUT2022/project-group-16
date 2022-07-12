package Models.Terrain;

import Models.Player.TileState;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Hex {
    private static Pane pane;
    private Position position;
    private TileType tileType;
    private TileState tileState;
    private TileFeature tileFeature;
    private ArrayList<ImageView> hexElements = new ArrayList<ImageView>();


    public Hex(Position position, Pane pane1){
        pane = pane1;
        this.position = position;
    }


    public void setTileFeature(TileFeature tileFeature) {
        if(tileFeature == null || tileFeature.equals(TileFeature.NONE) || this.tileState.equals(TileState.FOG_OF_WAR)) return;
        this.tileFeature = tileFeature;
        setFeatureBackground();
    }

    private void setFeatureBackground() {
        String url = "/photos/Tiles/Hexagon.png";
        switch (tileFeature){
            case FLOOD_PLAIN -> url = "/photos/FloodPlains.png";
            case FOREST -> url = "/photos/Forest.png";
            case JUNGLE -> url = "/photos/Jungle.png";
            case MARSH -> url = "/photos/Marsh.png";
            case OASIS -> url = "/photos/Oasis.png";
            case ICE -> url = "/photos/Ice.png";
        }
        Image image = new Image(this.getClass().getResource(url).toExternalForm());
        ImageView imageView = new ImageView();
        imageView.setX(position.X + 15);
        imageView.setY(position.Y + 5);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setImage(image);
        hexElements.add(imageView);
    }


    public void setTileState(TileState tileState) {
        this.tileState = tileState;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
        this.setBackground();
    }


    private void setBackground() {
        String url = "/photos/fog.png";
        if(this.tileState.equals(TileState.FOG_OF_WAR)){
            Image image = new Image(this.getClass().getResource(url).toExternalForm());
            ImageView imageView = new ImageView();
            imageView.setX(position.X);
            imageView.setY(position.Y);
            imageView.setFitWidth(90);
            imageView.setFitHeight(50);
            imageView.setImage(image);
            hexElements.add(imageView);
            return;
        }
        switch (this.tileType){
            case PLAINS -> url = "/photos/Tiles/Plains.png";
            case GRASSLAND ->  url = "/photos/Tiles/Grassland.png";
            case DESERT -> url = "/photos/Tiles/Desert.png";
            case TUNDRA -> url = "/photos/Tiles/Tundra.png";
            case SNOW ->  url = "/photos/Tiles/Snow.png";
            case HILLS -> url = "/photos/Tiles/Hill.png";
            case OCEAN -> url = "/photos/Tiles/Ocean.png";
            case MOUNTAIN -> url = "/photos/Tiles/Mountain.png";
            default -> url = "/photos/Tiles/Hexagon.png";
        }
        Image image = new Image(this.getClass().getResource(url).toExternalForm());
        ImageView imageView = new ImageView();
        imageView.setX(position.X);
        imageView.setY(position.Y);
        imageView.setFitWidth(90);
        imageView.setFitHeight(50);
        imageView.setImage(image);
        hexElements.add(imageView);
    }

    public void removeHex(){
        pane.getChildren().removeAll(hexElements);
    }
    public void addHex(){
        pane.getChildren().addAll(hexElements);
    }
}
