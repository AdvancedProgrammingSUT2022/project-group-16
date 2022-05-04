package Models.Units.NonCombatUnits;

import Models.City.City;

public class Settler extends NonCombatUnit{
    private void createCity(){
        City newCity = new City(this.getTile(), this.getRulerPlayer());
        this.getRulerPlayer().addCity(newCity);
        // TODO: delete settler from tile
    }

    
    @Override
    public String toString()
    {
        return "SETTLER";
    }
}
