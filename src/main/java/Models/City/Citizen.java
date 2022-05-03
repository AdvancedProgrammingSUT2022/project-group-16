package Models.City;

import Models.Terrain.Tile;

public class Citizen
{
	private City City;
	private Tile workingTile;

	public Citizen(City city) {
		this.City = city;
		workingTile = null;
	}

	public Tile getWorkingTile() {
		return workingTile;
	}
	public City getCity() {
		return City;
	}
	public void setCitizenOnTile(Tile workingTile) {
		this.workingTile = workingTile;
	}
	public void unEmployCitizen(){
		this.workingTile = null;
	}
}
