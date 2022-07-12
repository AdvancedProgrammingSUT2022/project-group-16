package Models.Units.CombatUnits;

import Models.City.City;
import Models.Player.Player;
import Models.Terrain.Tile;

public class LongRange extends CombatUnit{
    private LongRangeType type;
    private boolean isSet = false;
    private City targetCity = null;
    private int setCounter = 0;

    public LongRange(Player rulerPlayer, LongRangeType longRangeType, Tile tile){
        this.setRulerPlayer(rulerPlayer);
        this.setType(longRangeType);
        this.setProductionCost(type.cost);
        this.setRequiredTechnology(type.requiredTech);
        this.setMovementPoints(type.movement);
        this.setTile(tile);
        tile.setCombatUnitInTile(this);
        this.setPower(type.combatStrength);
        //this.setRequiredResource();
        rulerPlayer.addUnit(this);
    }
    //for mocking a unit while constructing in city
    public LongRange() {

    }

    public boolean isSet() {
        return isSet;
    }

    public void setIsSet(boolean set) {
        isSet = set;
    }

    public int getSetCounter() {
        return setCounter;
    }

    public void setSet(int set) {
        setCounter = set;
    }

    public City getTargetCity() {
        return targetCity;
    }

    public void setTargetCity(City targetCity) {
        this.targetCity = targetCity;
    }

    public LongRangeType getType() {
        return type;
    }

    public void setType(LongRangeType type) {
        this.type = type;
    }

    public LongRange(LongRangeType type){
        this.type = type;
    }

    public void getReadyToFight(){
        this.isSet = true;
    }

    @Override
    public String toString()
    {
        return type.name();
    }
    public LongRange clone(){
        LongRange newLongRange = new LongRange(this.getRulerPlayer(),this.getType(),this.getTile());
        newLongRange.setIsSet(this.isSet);
        newLongRange.setHealth(this.getHealth());
        return newLongRange;
    }
}
