package Models.City;

import Models.Terrain.Tile;

public class Citizen
{
	private City City;
	private Tile workingTile;
	private boolean isExpert;

	public Citizen(City city) {
		this.City = city;
		workingTile = null;
		isExpert = false;
	}

	public Tile getWorkingTile() {
		return workingTile;
	}


	public City getCity() {
		return City;
	}

	public void setCity(Models.City.City city) {
		City = city;
	}

	public void setCitizenOnTile(Tile workingTile) {
		this.workingTile = workingTile;
	}

	public void unEmployCitizen(){
		this.workingTile = null;
	}


}
