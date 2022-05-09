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
        new City(this.getTile(), this.getRulerPlayer());
        getRulerPlayer().setSelectedUnit(null);
        this.removeUnit();
    }

    
    @Override
    public String toString()
    {
        return "SETTLER";
    }
    public Settler clone()
    {
        Settler settler = new Settler(this.getRulerPlayer(), this.getTile());
        settler.setHealth(this.getHealth());
        return settler;
    }
}
