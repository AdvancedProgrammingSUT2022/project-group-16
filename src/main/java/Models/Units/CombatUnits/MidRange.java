package Models.Units.CombatUnits;

import Models.City.City;
import Models.Player.Player;
import Models.Terrain.Tile;
import Models.Units.Unit;
import Models.Units.UnitState;

public class MidRange extends CombatUnit{
    private MidRangeType type;

    public MidRange(Player rulerPlayer, MidRangeType midRangeType, Tile tile){
        this.setRulerPlayer(rulerPlayer);
        this.setType(midRangeType);
        this.setProductionCost(type.cost);
        this.setRequiredTechnology(type.requiredTech);
        this.setMovementPoints(type.movement);
        this.setPower(type.combatStrength);
        this.setTile(tile);
        tile.setCombatUnitInTile(this);
        //this.setRequiredResource();
        rulerPlayer.addUnit(this);
    }
    //for mocking a unit while constructing in city
    public MidRange() {

    }

    public MidRangeType getType() {
        return type;
    }

    private void setType(MidRangeType midRangeType) {
        this.type = midRangeType;
    }

    public void pillage(){
        this.getTile().setRuined(true);
    }

    public void attack(CombatUnit unit){
        this.setMovementPoints(0);
        this.setXP(this.getXP() + 10);
        unit.setXP(unit.getXP() + 10);
        int myPower = this.getPower() + (int) ( (double)(this.getTile().getTileType().combatModifier * this.getPower()) / 100.0) +
                (int) ( (double)(this.getTile().getTileFeature().combatModifier * this.getPower()) / 100.0);
        myPower = (1 - ((MAX_HEALTH - this.getHealth()) / 10 ))* myPower;
        int enemyPower = unit.getPower() + (int) ( (double)(unit.getTile().getTileType().combatModifier * unit.getPower()) / 100.0) +
                (int) ( (double)(unit.getTile().getTileFeature().combatModifier * unit.getPower()) / 100.0);
        enemyPower = (1 - ((unit.MAX_HEALTH - unit.getHealth()) / 10 ))* enemyPower;
        this.setHealth(this.getHealth() - enemyPower);
        unit.setHealth(unit.getHealth() - myPower);
        if(this.getHealth() <= 0){
            this.destroy();
        }
        if(unit.getHealth() <= 0 && this.getHealth() > 0){
            this.setMovementPoints(type.movement);
            Tile destination = unit.getTile();
            calculateXPs(destination);
            unit.destroy();
            this.move(destination);
            destination.getNonCombatUnitInTile().setUnitState(UnitState.HOSTAGE);
        }
    }

    @Override
    public String toString()
    {
        return type.name();
    }
    public MidRange clone(){
        MidRange newMidRange = new MidRange(this.getRulerPlayer(),this.getType(),this.getTile());
        newMidRange.setHealth(this.getHealth());
        return newMidRange;
    }
}
