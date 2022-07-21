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
        this.setMP(type.movement);
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
    public MidRange(MidRangeType midRangeType) {
        this.type = midRangeType;
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

        //primary power
        int myPower = this.getPower() + (int) ( (double)(this.getTile().getTileType().combatModifier * this.getPower()) / 100.0) +
                (int) ( (double)(this.getTile().getTileFeature().combatModifier * this.getPower()) / 100.0);
        //final power
        myPower = (1 - ((MAX_HEALTH - this.getHealth()) / 10 ))* myPower;
        //primary enemy power
        int enemyPower = unit.getPower() + (int) ( (double)(unit.getTile().getTileType().combatModifier * unit.getPower()) / 100.0) +
                (int) ( (double)(unit.getTile().getTileFeature().combatModifier * unit.getPower()) / 100.0);
        //final enemy power
        enemyPower = (1 - ((unit.MAX_HEALTH - unit.getHealth()) / 10 ))* enemyPower;
        int enemyHealth = unit.getHealth() - myPower;
        int myHealth = this.getHealth() - enemyPower;

        if (enemyHealth <= 0) {
            this.setMovementPoints(type.movement);
            Tile destination = unit.getTile();
            calculateXPs(destination);
            unit.destroy();
            this.move(destination);
            destination.setCombatUnitInTile(this);
            if (destination.getNonCombatUnitInTile() != null) {
                destination.getNonCombatUnitInTile().setUnitState(UnitState.HOSTAGE);
            }
        }
        else if (myHealth <= 0)
        {
            unit.setMovementPoints(type.movement);
            Tile destination = this.getTile();
            calculateXPs(destination);
            this.destroy();
            unit.move(destination);
            destination.setCombatUnitInTile(unit);
            if (destination.getNonCombatUnitInTile() != null)
                destination.getNonCombatUnitInTile().setUnitState(UnitState.HOSTAGE);
        }
        else {
            this.setHealth(myHealth);
            unit.setHealth(enemyHealth);
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
