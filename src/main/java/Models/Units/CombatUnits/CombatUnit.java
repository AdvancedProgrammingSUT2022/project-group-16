package Models.Units.CombatUnits;

import Controllers.GameController;
import Models.City.City;
import Models.Player.Notification;
import Models.Units.Unit;
import enums.gameCommands.unitCommands;


public abstract class CombatUnit extends Unit
{
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
    
    public abstract CombatUnit clone();
}
