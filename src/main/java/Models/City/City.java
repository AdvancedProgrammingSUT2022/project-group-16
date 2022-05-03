package Models.City;

import Models.Player.Player;
import Models.Resources.Resource;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;

import java.util.ArrayList;

public class City
{
	ArrayList<Tile> territory;
	Tile capitalTile;
	int foodYield;
	int productionYield;
	int goldYield;
	ArrayList<Building> buildings;
	ArrayList<Citizen> citizens;
	ArrayList<Tile> workingTiles;
	Constructable currentConstruction = null;
	Product currentProduct = null; //what the city is producing
	CombatUnit garrison = null ;
	NonCombatUnit nonCombatUnit = null;
	int combatStrength;
	Player rulerPlayer;
	String name;
	
	public City(Tile capitalTile, Player rulerPlayer)
	{
		this.capitalTile = capitalTile;
		this.rulerPlayer = rulerPlayer;
		territory = new ArrayList<>();
		territory.add(capitalTile); //TODO: also add adjacent tiles
		foodYield = 0;
		productionYield = 0;
		goldYield = 0;
		buildings = new ArrayList<>();
		citizens = new ArrayList<Citizen>();
	}

	public String getName() {
		return name;
	}

	public void createBuilding() //TODO
	{
	
	}

	public int getFoodYield() {
		return foodYield;
	}
	public int getGoldYield() {
		return goldYield;
	}
	public int getProductionYield() {
		return productionYield;
	}


	public void growCity() //TODO
	{
	
	}
	public CombatUnit getGarrison() {
		return garrison;
	}

	public void setGarrison(CombatUnit garrison) {
		this.garrison = garrison;
		//TODO add defence power to city
	}
	public NonCombatUnit getNonCombatUnit() {
		return nonCombatUnit;
	}

	public void setNonCombatUnit(NonCombatUnit nonCombatUnit) {
		this.nonCombatUnit = nonCombatUnit;
	}

	public Tile getCapitalTile() {
		return capitalTile;
	}

	public void buyTile(Tile tile){
		//TODO check if player can pay the cost
		if(isTileNeighbor(tile))
			this.territory.add(tile);

	}

	private boolean isTileNeighbor(Tile newTile){
		for (Tile tile : territory) {
			if(tile.getPosition().Q - newTile.getPosition().Q == 1 || tile.getPosition().Q - newTile.getPosition().Q == -1 ||
					tile.getPosition().R - newTile.getPosition().R == 1 || tile.getPosition().R - newTile.getPosition().R == -1 ||
					tile.getPosition().S - newTile.getPosition().S == 1 || tile.getPosition().S - newTile.getPosition().S == -1 )
				return true;
		}
		return false;
	}

	public void buyProduct(Product product){
		//TODO
	}

}





















