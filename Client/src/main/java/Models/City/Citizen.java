package Models.City;

import Models.Terrain.Tile;
import enums.gameEnum;

public class Citizen {
    transient private City city;
    private Tile workingTile;

    public Citizen(City city) {
        this.city = city;
        workingTile = null;
    }

    public Tile getWorkingTile() {
        return workingTile;
    }
    public void setWorkingTile(Tile workingTile)
    {
        this.workingTile = workingTile;
    }
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String setCitizenOnTile(Tile workingTile) {
        if (!doesAnotherCitizenWorkOnTile(workingTile)) {
            if (isCityNearTile(workingTile)) {
                this.workingTile = workingTile;
                return gameEnum.successfulLock.regex;
            }
            return gameEnum.farTile.regex;
        }
        return gameEnum.anotherCitizenWorking.regex;
    }

    private boolean isCityNearTile(Tile workingTile) {
        for (Tile tile : this.getCity().getTerritory()) {
            if (tile.distanceTo(workingTile) <= 2) return true;
        }
        return false;
    }

    public void unEmployCitizen() {
        this.workingTile = null;
    }

    private boolean doesAnotherCitizenWorkOnTile(Tile workingTile) {
        for (City city : this.city.getRulerPlayer().getCities()) {
            for (Citizen citizen : city.getCitizens()) {
                if (citizen.workingTile == workingTile) return true;
            }
        }
        return false;
    }
}
