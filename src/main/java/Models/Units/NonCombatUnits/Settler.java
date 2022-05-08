package Models.Units.NonCombatUnits;

import Models.City.City;
import Models.Player.Player;
import Models.Terrain.Tile;

public class Settler extends NonCombatUnit{
    public Settler(Player rulerPlayer, int cost, int movement, Tile tile, int speed, int power){
        this.setRulerPlayer(rulerPlayer);
        this.setProductionCost(cost);
        this.setRequiredTechnology(null);
        this.setMovementPoints(movement);
        this.setTile(tile);
        this.setHealth(100); //TODO what is the max health,speed,power?
        this.setSpeed(speed);
        this.setPower(power);
        this.setRequiredResource(null);
        rulerPlayer.addUnit(this);
    }

    public void createCity()
    {
        City newCity = new City(this.getTile(), this.getRulerPlayer());
        // TODO: delete settler from tile
    }

    
    @Override
    public String toString()
    {
        return "SETTLER";
    }
}
