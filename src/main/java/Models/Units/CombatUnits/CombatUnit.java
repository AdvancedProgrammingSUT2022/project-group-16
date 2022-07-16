package Models.Units.CombatUnits;

import Controllers.GameController;
import Models.City.City;
import Models.Player.Notification;
import Models.Terrain.BorderType;
import Models.Terrain.Tile;
import Models.Terrain.TileFeature;
import Models.Terrain.TileType;
import Models.Units.CommandHandeling.UnitCommands;
import Models.Units.Unit;
import Models.Units.UnitState;
import enums.gameCommands.unitCommands;


public abstract class CombatUnit extends Unit
{
    private int defencePower = 10;

    public int getDefencePower() {
        return defencePower;
    }

    public void setDefencePower(int defencePower) {
        this.defencePower = defencePower;
    }

    public String attackToCity(City city, GameController gameController)
    {
        if(city.getHitPoints() == 1 && this.getClass().equals(MidRange.class))
        {
            //seized city
            this.getRulerPlayer().getSelectedUnit().setTile(city.getCapitalTile());
            city.seizeCity(this.getRulerPlayer());
            return unitCommands.citySeized.regex;
        }
        else if(city.getHitPoints() == 1)
            return unitCommands.longRangeSeizedCity.regex;
        else if(this.getClass().equals(MidRange.class)) //midrange attack
        {
            int y = ((MidRange) this).getType().getCombatStrength();
            double x = (double) (10 + this.getHealth()) / 20;
            int cityDamage = (city.getCombatStrength() - (int) (x * y));
            int unitDamage = ((((MidRange) this).getType().getCombatStrength()) - city.getCombatStrength());
            this.getRulerPlayer().setXP(this.getRulerPlayer().getXP() + 1);
            city.getRulerPlayer().setXP(this.getRulerPlayer().getXP() + 1);
            if(cityDamage < 0)
            {
                city.setHitPoints(city.getHitPoints() + cityDamage);
                if(city.getHitPoints() < 1)
                {
                    this.getRulerPlayer().getSelectedUnit().setTile(city.getCapitalTile());
                    city.seizeCity(this.getRulerPlayer());
                    new Notification(this.getRulerPlayer(), gameController.getTurnCounter(), "you seized " + city.getName());
                    return null;
                }
            }
            if(unitDamage < 0)
            {
                this.setHealth(this.getHealth() + unitDamage);
                if(this.getHealth() <= 0)
                {
                    destroy();
                    new Notification(this.getRulerPlayer(), gameController.getTurnCounter(), "your unit destroyed:(");
                    return unitCommands.unitDestroy.regex;
                }
            }
        }
        else //long range attack
        {
            if(!isUnitNearCity(city)) return unitCommands.rangeError.regex;
            int cityDamage = (city.getCombatStrength() - (((LongRange) this).getType().getCombatStrength() * ((10 - this.getHealth()) / 20)));
            this.getRulerPlayer().setXP(this.getRulerPlayer().getXP() + 1);
            city.getRulerPlayer().setXP(this.getRulerPlayer().getXP() + 1);
            if(cityDamage < 0)
            {
                city.setHitPoints(city.getHitPoints() + cityDamage);
                if(city.getHitPoints() < 1) {
                    city.setHitPoints(1);
                    return unitCommands.longRangeSeizedCity.regex;
                }
            }
        }
        return unitCommands.successfullAttack.regex;
    }

    private boolean isUnitNearCity(City city) {
        for (Tile tile : city.getTerritory()) {
            if(tile.distanceTo(this.getTile()) <= ((LongRange) this).getType().range) return true;
        }
        return false;
    }

    public String fortify(){
        if(this instanceof MidRange && (((MidRange) this).getType().equals(MidRangeType.HORSEMAN) || ((MidRange) this).getType().equals(MidRangeType.SWORDSMAN)))
            return "cannot fortify this unit";
        if(this.getUnitState().equals(UnitState.FORTIFIED)){
            this.setDefencePower((int) (this.getDefencePower() * 1.5));
        }
        else{
            this.setDefencePower((int) (this.getDefencePower() * 1.25));
        }
        this.setUnitState(UnitState.FORTIFIED);
        return null;
    }
    public String fortifyTillHeel(){
        if(this instanceof MidRange && (((MidRange) this).getType().equals(MidRangeType.HORSEMAN) || ((MidRange) this).getType().equals(MidRangeType.SWORDSMAN)))
            return "cannot fortify this unit";
        if(this.getHealth() >= this.MAX_HEALTH){
            this.setUnitState(UnitState.ACTIVE);
            this.setHealth(this.MAX_HEALTH);
            return "unit health is complete";
        }
        if(this.getUnitState().equals(UnitState.FORTIFIED_FOR_HEALING)){
            this.setHealth((int) (this.getHealth() * 1.5));
        }
        else{
            this.setHealth((int) (this.getHealth() * 1.25));
        }
        this.setUnitState(UnitState.FORTIFIED_FOR_HEALING);
        return null;
    }

    public void calculateXPs(Tile winningTile){
        if(winningTile.getTileType().equals(TileType.HILLS) ||winningTile.getTileType().equals(TileType.MOUNTAIN) ||
                winningTile.getTileFeature().equals(TileFeature.JUNGLE)){
            this.setXP(this.getXP() + 20); //bonus
        }
        if(this.getTile().getBoarderType(winningTile) != null && this.getTile().getBoarderType(winningTile).equals(BorderType.RIVER))
            this.setXP(this.getXP() - 10); //penalty
        if(this.getTile().getTileType().equals(TileType.HILLS))
            this.setXP(this.getXP() + 10); //bonus
    }
    
    public abstract CombatUnit clone();
}
