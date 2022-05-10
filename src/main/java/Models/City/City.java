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
	private int cupYield = 0;
	private int population = 0;
	private int power = 0;
	private Building building;
	private final ArrayList<Building> buildings = new ArrayList<>();
	private final ArrayList<Citizen> citizens = new ArrayList<>();
	private final ArrayList<Tile> workingTiles = new ArrayList<>();
	private Constructable currentConstruction = null;
	private int inLineConstructionTurn; //how many turns are left till the construction is ready

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
		this.name = setCityName();
		rulerPlayer.addCity(this);
		if(rulerPlayer.getCities().size() == 1)
		{
			cupYield = 3;
			rulerPlayer.setCup(3);
			rulerPlayer.setCurrentCapitalCity(this);
		}
	}
	private String setCityName() {
		for (String cityName : rulerPlayer.getCivilization().cities) {
			int flg = 0;
			for (City city : rulerPlayer.getCities()) {
				if(city.getName().equals(cityName)){
					flg = 1;
					break;
				}
			}
			if(flg == 0){
				return cityName;
			}
		}
		return null; // there is no more city
	}


	public String getName() {
		return name;
	}
	public ArrayList<Tile> getTerritory()
	{
		return territory;
	}
	public int getPower() {
		return power;
	}
	public void addPower(int amount) {
		power += amount;
	}
	public void addProduction(int amount) {
		productionYield += amount;
	}
	public void addGold(int amount) {
		goldYield += amount;
	}
	public void addFood(int amount) {
		foodYield += amount;
	}

	public void createBuilding(Building building) //TODO
	{
		buildings.add(building);
		System.out.println(building.getBuildingType());
		System.out.println(building.happinessFromBuilding(building.getBuildingType()));
		rulerPlayer.setHappiness(rulerPlayer.getHappiness() + building.happinessFromBuilding(building.getBuildingType())); //Increase happiness
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
	public int getCupYield() {
		return cupYield;
	}
	public int getPopulation() {
		return population;
	}
	public void addPopulation(int amount)
	{
		this.population += amount;
		cupYield += amount;
		rulerPlayer.setCup(rulerPlayer.getCup() + amount);
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
	private boolean isTileNeighbor(Tile newTile){ //TODO: should be deleted
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





















