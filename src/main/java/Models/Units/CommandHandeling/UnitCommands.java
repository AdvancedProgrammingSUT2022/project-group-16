package Models.Units.CommandHandeling;

import Models.Terrain.Tile;
import Models.Units.Unit;

import java.security.PublicKey;

public enum UnitCommands {
    GET_READY(),
    FORTIFY(),
    FORTIFY_TILL_Heel(),
    GET_SET(),
    AWAKEN(),
    REMOVE_UNIT(),
    MOVE(),
    ATTACK(),
    GET_READY_TO_FIGHT(),
    CREATE_CITY(),
    BUILD_ROAD(),
    BUILD_RAILROAD(),
    BUILD_FARM(),
    BUILD_MINE(),
    BUILD_TRADING_POST(),
    BUILD_LUMBER_MILL(),
    BUILD_PASTURE(),
    BUILD_CAMP(),
    BUILD_PLANTATION(),
    BUILD_QUARRY(),
    BUILD_FACTORY(),
    REMOVE_JUNGLE(),
    REMOVE_FOREST(),
    REMOVE_MARSH(),
    REMOVE_ROADS(),
    CHANGE_ACTIVATE(),
    SLEEP(),
    REPAIR_TILE();


    private Tile movementDestination ;

    public Tile getDestination() {
        return movementDestination;
    }

    public void setDestination(Tile destination) {
        this.movementDestination = destination;
    }
}
