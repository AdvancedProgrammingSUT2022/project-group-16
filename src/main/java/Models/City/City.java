package Models.City;

import Controllers.GameController;
import Models.Player.Player;
import Models.Resources.Resource;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;

import java.util.ArrayList;

public class City
{
	private final ArrayList<Tile> territory = new ArrayList<>();
	private Tile capitalTile;
	private int foodYield = 0;
	private int productionYield = 0;
	private int goldYield = 0;
	private final ArrayList<Building> buildings = new ArrayList<>();
	private final ArrayList<Citizen> citizens = new ArrayList<>();
	private final ArrayList<Tile> workingTiles = new ArrayList<>();
	private Constructable currentConstruction = null;
	private Product currentProduct = null; //what the city is producing
	private CombatUnit garrison = null;
	private NonCombatUnit nonCombatUnit = null;
	private int combatStrength;
	private Player rulerPlayer;
	private String name;
	
	public City(Tile capitalTile, Player rulerPlayer)
	{
		this.capitalTile = capitalTile;
		this.rulerPlayer = rulerPlayer;
		territory.add(capitalTile);
		territory.addAll(GameController.getInstance().getAdjacentTiles(capitalTile));
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


	public void growCity() //TODO: this should increase the number of citizens of the city
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





















