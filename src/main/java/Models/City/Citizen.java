package Models.City;

import Models.Terrain.Tile;

public class Citizen
{
	private final City city;
	private Tile workingTile;

	public Citizen(City city) {
		this.city = city;
		workingTile = null;
	}

	public Tile getWorkingTile() {
		return workingTile;
	}
	public City getCity() {
		return city;
	}
	public String setCitizenOnTile(Tile workingTile) {
		if(!doesAnotherCitizenWorkOnTile(workingTile)) {
			if(isCityNearTile(workingTile)) {
				this.workingTile = workingTile;
				return null;
			}
			return"the tile is far from city";
		}
		return "another citizen is working in tile";
	}

	private boolean isCityNearTile(Tile workingTile) {
		for (Tile tile : this.getCity().getTerritory()) {
			if(tile.distanceTo(workingTile) <= 2) return true;
		}
		return false;
	}

	public void unEmployCitizen(){
		this.workingTile = null;
	}

	private boolean doesAnotherCitizenWorkOnTile(Tile workingTile){
		for (Models.City.City city : this.City.getRulerPlayer().getCities()) {
			for (Citizen citizen : city.getCitizens()) {
				if(citizen.workingTile == workingTile) return true;
			}
		}
		return false;
	}
}
