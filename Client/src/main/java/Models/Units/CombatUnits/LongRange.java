package Models.Units.CombatUnits;

import Models.City.City;
import Models.Player.Player;
import Models.Terrain.Tile;
import Models.Units.UnitState;

public class LongRange extends CombatUnit {
    private LongRangeType type;
    private boolean isSet = false;
    private City targetCity = null;
    private int setCounter = 0;

    public LongRange(Player rulerPlayer, LongRangeType longRangeType, Tile tile) {
        this.setRulerPlayer(rulerPlayer);
        this.setType(longRangeType);
        this.setMP(type.movement);
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
    public LongRange(LongRangeType type) {
        this.type = type;
    }

    public String attack(CombatUnit unit) {
        if (this.getTile().distanceTo(unit.getTile()) > type.range) return "not in the range";
        this.setMovementPoints(0);
        this.setXP(this.getXP() + 10);
        unit.setXP(unit.getXP() + 10);
        int myPower = this.type.rangedCombatStrength + (int) ((double) (this.getTile().getTileType().combatModifier * this.type.rangedCombatStrength) / 100.0) +
                (int) ((double) (this.getTile().getTileFeature().combatModifier * this.type.rangedCombatStrength) / 100.0);
        unit.setHealth(unit.getHealth() - myPower);

        if (unit.getHealth() <= 0 && this.getHealth() > 0) {
            this.setMovementPoints(type.movement);
            Tile destination = unit.getTile();
            calculateXPs(destination);
            unit.destroy();
            destination.getNonCombatUnitInTile().setUnitState(UnitState.HOSTAGE);
        }
        return null;
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

    @Override
    public String toString() {
        return type.name();
    }

    public LongRange clone() {
        LongRange newLongRange = new LongRange(this.getRulerPlayer(), this.getType(), this.getTile());
        newLongRange.setIsSet(this.isSet);
        newLongRange.setHealth(this.getHealth());
        return newLongRange;
    }
}
