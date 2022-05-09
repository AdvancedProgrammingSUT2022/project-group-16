package Models.Units.NonCombatUnits;

import Models.City.City;
import Models.Player.Player;
import Models.Terrain.Tile;

public class Settler extends NonCombatUnit{
    public Settler(Player rulerPlayer, Tile tile){
        this.setRulerPlayer(rulerPlayer);
        this.setProductionCost(10);//TODO what is the max health/cost??
        this.setRequiredTechnology(null);
        this.setMovementPoints(1);
        this.setTile(tile);
        this.setHealth(100);
        this.setSpeed(1);
        this.setPower(0);
        this.setRequiredResource(null);
        rulerPlayer.addUnit(this);
    }

    public void createCity()
    {
        //TODO check if the city can be created in the tile
        new City(this.getTile(), this.getRulerPlayer());
        this.removeUnit();
    }

    
    @Override
    public String toString()
    {
        return "SETTLER";
    }
}
