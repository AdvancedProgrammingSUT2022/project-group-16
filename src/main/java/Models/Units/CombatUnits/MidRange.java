package Models.Units.CombatUnits;

import Models.Player.Player;
import Models.Terrain.Tile;

public class MidRange extends CombatUnit{
    private MidRangeType type;

    public MidRange(Player rulerPlayer, MidRangeType midRangeType, Tile tile, int speed, int power){
        this.setRulerPlayer(rulerPlayer);
        this.setType(midRangeType);
        this.setProductionCost(type.cost);
        this.setRequiredTechnology(type.requiredTech);
        this.setMovementPoints(type.movement);
        this.setTile(tile);
        tile.setCombatUnitInTile(this);
        this.setHealth(100); //TODO what is the max health,speed,power?
        this.setSpeed(speed);
        this.setPower(power);
        //this.setRequiredResource();
        rulerPlayer.addUnit(this);
    }

    public MidRangeType getType() {
        return type;
    }

    private void setType(MidRangeType midRangeType) {
        this.type = midRangeType;
    }

    private void pillage(){

    }
    
    @Override
    public String toString()
    {
        return type.name();
    }
    public MidRange clone(){
        MidRange newMidRange = new MidRange(this.getRulerPlayer(),this.getType(),this.getTile(),this.getSpeed(),this.getPower());
        newMidRange.setHealth(this.getHealth());
        return newMidRange;
    }
}
