package Models.Terrain;

import Controllers.GameController;
import Models.Player.TileState;
import javafx.event.EventHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Hex {
    private static Pane parent;
    private final Pane pane;
    private final Position position;
    private Tile tile;
    private TileState tileState;
    private ArrayList<ImageView> hexElements = new ArrayList<>();


    public Hex(Position position){
        this.position = position;
        this.pane = new Pane();
        pane.setPrefWidth(90);
        pane.setPrefHeight(50);
        pane.setLayoutX(position.X);
        pane.setLayoutY(position.Y);
    }

    public static void setPane(Pane pane){
        parent = pane;
    }
    public void setTile(Tile tile){
        this.tile = tile;
        setBackground();
        setFeatureBackground();
        //setBoarders();
    }

    private ImageView setImage(String url, int x, int y ,int width, int height){
        Image image = new Image(this.getClass().getResource(url).toExternalForm());
        ImageView imageView = new ImageView();
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setImage(image);
        hexElements.add(imageView);
        return imageView;
    }

    private void setBoarders() {
        if(this.tileState.equals(TileState.FOG_OF_WAR)) return;
        for (int i = 0; i < 6; i++) {
            if(tile.getBorders()[i].equals(BorderType.RIVER)){
                String url = (i % 3 == 0 ? "/photos/Boarders/River-Bottom.png" :
                        (i % 3 == 1? "/photos/Boarders/River-BottomRight.png" : "/photos/Boarders/River-BottomLeft.png"));
                Position p = findCoordinates(i);
                setImage(url,p.X, p.Y, 54, 10);
            }
        }
    }

    private Position findCoordinates(int i) {
        int x = 0, y = 0;
        switch (i){
            case 0:
                x = position.X + 20;
                y = position.Y - 10;
                break;
            case 1:
                x = position.X - 40;
                y = position.Y;
                break;
            case 2:
                x = position.X - 40;
                y = position.Y + 60;
                break;
            case 3:
                x = position.X + 20;
                y = position.Y + 50;
                break;
            case 4:
                x = position.X + 130;
                y = position.Y + 60;
                break;
            case 5:
                x = position.X + 130;
                y = position.Y;
                break;
        }
        return new Position(x,y);
    }

    private void setFeatureBackground() {
        if(tile.getTileFeature() == null || tile.getTileFeature().equals(TileFeature.NONE) || this.tileState.equals(TileState.FOG_OF_WAR)) return;
        String url = "/photos/Tiles/Hexagon.png";
        switch (tile.getTileFeature()){
            case FLOOD_PLAIN -> url = "/photos/features/FloodPlains.png";
            case FOREST -> url = "/photos/features/Forest.png";
            case JUNGLE -> url = "/photos/features/Jungle.png";
            case MARSH -> url = "/photos/features/Marsh.png";
            case OASIS -> url = "/photos/features/Oasis.png";
            case ICE -> url = "/photos/features/Ice.png";
        }
        setImage(url, position.X +15,position.Y + 5, 30, 30 );
    }


    public void setTileState(TileState tileState) {
        this.tileState = tileState;
    }



    private void setBackground() {
        String url = "/photos/features/fog.png";
        if(this.tileState.equals(TileState.FOG_OF_WAR)){
            setImage(url, position.X, position.Y , 90, 50);
            return;
        }
        switch (this.tile.getTileType()){
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
        ImageView imageView = setImage(url, position.X, position.Y, 90,50);
        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(imageView.getEffect() != null){
                    imageView.setEffect(null);
                    //add function to remove tile description;
                }else {
                    imageView.setEffect(new Lighting());
                    //add function to show tile description;
                }
            }
        });
    }

    public void removeHex(){
        parent.getChildren().remove(pane);
        pane.getChildren().removeAll(hexElements);
        this.hexElements.clear();
        this.tileState  =null;
        this.tile = null;
    }
    public void addHex(){
        pane.getChildren().addAll(hexElements);
        parent.getChildren().add(pane);
    }
}
