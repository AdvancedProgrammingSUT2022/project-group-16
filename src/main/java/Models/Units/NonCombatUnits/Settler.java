package Models.Units.NonCombatUnits;

import Models.City.City;

public class Settler extends NonCombatUnit{
    private void createCity(){
        City newCity = new City(this.getTile(), this.getRulerPlayer());
        this.getRulerPlayer().addCity(newCity);
    }

    @Override
    protected void move() {

    }
    
    @Override
    public String toString()
    {
        return "SETTLER";
    }
}
