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
	public void setCitizenOnTile(Tile workingTile) {
		this.workingTile = workingTile;
	}
	public void unEmployCitizen(){
		this.workingTile = null;
	}
}
