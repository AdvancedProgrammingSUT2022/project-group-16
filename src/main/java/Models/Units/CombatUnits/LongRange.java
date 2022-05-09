package Models.Units.CombatUnits;

import Models.Player.Player;
import Models.Terrain.Tile;

public class LongRange extends CombatUnit{
    private LongRangeType type;
    private boolean isSet = false;

    public LongRange(Player rulerPlayer, LongRangeType longRangeType, Tile tile, int speed, int power){
        this.setRulerPlayer(rulerPlayer);
        this.setType(longRangeType);
        this.setProductionCost(type.cost);
        this.setRequiredTechnology(type.requiredTech);
        this.setMovementPoints(type.movement);
        this.setTile(tile);
        this.setHealth(100); //TODO what is the max health,speed,power?
        this.setSpeed(speed);
        this.setPower(power);
        //this.setRequiredResource();
        rulerPlayer.addUnit(this);
    }

    public boolean isSet() {
        return isSet;
    }

    public void setIsSet(boolean set) {
        isSet = set;
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
    
    private void getReadyToFight(){
        this.isSet = true;
    }
    
    @Override
    public String toString()
    {
        return type.name();
    }
    public LongRange clone(){
        LongRange newLongRange = new LongRange(this.getRulerPlayer(),this.getType(),this.getTile(),this.getSpeed(),this.getPower());
        newLongRange.setIsSet(this.isSet);
        newLongRange.setHealth(this.getHealth());
        return newLongRange;
    }
}
