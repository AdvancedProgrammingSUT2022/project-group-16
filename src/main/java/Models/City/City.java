package Models.City;

import Controllers.GameController;
import Models.Player.Player;
import Models.Terrain.Tile;
import Models.Terrain.TileType;
import Models.Units.CombatUnits.*;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.Unit;
import Models.Units.UnitState;

import java.util.ArrayList;

public class City
{
	private final ArrayList<Tile> territory = new ArrayList<>();
	private Tile capitalTile;
	private int foodYield = 0;
	private int productionYield = 0;
	private int goldYield = 0;
	private int cupYield = 0;
	private int hitPoints = 20;
	private  ArrayList<Building> buildings = new ArrayList<>();
	private  ArrayList<Citizen> citizens = new ArrayList<>();
	private Construction currentConstruction = null;
	private int inLineConstructionTurn = 4; //how many turns are left till the construction is ready
	private Product currentProduct = null; //what the city is producing
	private CombatUnit garrison = null;
	private NonCombatUnit nonCombatUnit = null;
	private int combatStrength = 10;//this amount is default and may change later
	private Player rulerPlayer;
	private String name;
	private CityState state = CityState.NONE;

	public City(Tile capitalTile, Player rulerPlayer)
	{
		//TODO: check that we can't build a city in distance of 1 cities
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


	public ArrayList<Citizen> getCitizens() {
		return citizens;
	}
	public void addCitizen(Citizen citizen)
	{
		citizens.add(citizen);
	}

	public CityState getState() {
		return state;
	}

	public int employedCitizens()
	{
		int n = 0;
		for(Citizen citizen : citizens)
			if(citizen.getWorkingTile() != null)
				n++;
		return n;
	}
	public String getName() {
		return name;
	}
	public ArrayList<Tile> getTerritory()
	{
		return territory;
	}
	public int getCombatStrength() {
		return combatStrength;
	}
	public void setCombatStrength(int power) {
		this.combatStrength = power;
	}
	public Construction getCurrentConstruction() {
		return currentConstruction;
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
	public int getHitPoints() {
		return hitPoints;
	}
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}

	public void createBuilding(Building building) //TODO
	{
		buildings.add(building);
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
	public Player getRulerPlayer() {
		return rulerPlayer;
	}
	public void setRulerPlayer(Player rulerPlayer) {
		this.rulerPlayer =  rulerPlayer;
	}
	public void addPopulation(int amount)
	{
		for(int i = 0; i < amount; i++){
			citizens.add(new Citizen(this));
		}
		cupYield += amount;
		rulerPlayer.setCup(rulerPlayer.getCup() + amount);
	}

	public ArrayList<Building> getBuildings() {
		return buildings;
	}

	public void growCity() //TODO: this should increase the number of citizens of the city
	{

	}
	public CombatUnit getGarrison() {
		return garrison;
	}
	public void setGarrison(CombatUnit garrison) {
		this.garrison = garrison;
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
	public void purchaseTile(Tile tile){
		if(isTileNeighbor(tile) && getRulerPlayer().getGold() >= getRulerPlayer().getTilePurchaseCost()) {
			this.territory.add(tile);
			this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - getRulerPlayer().getTilePurchaseCost());
			this.getRulerPlayer().setTilePurchaseCost((int) (1.2 * getRulerPlayer().getTilePurchaseCost()));
		}

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
	public String buyProduct(Product product){
		//TODO add purchasing buildings for phase 2
		if( product instanceof Unit){
			if(this.getRulerPlayer().getGold() >= ((Unit) product).getProductionCost()) {
				Tile destination;
				if((destination = (findTileWithNoUnit((Unit) product))) == null) return"there is no tile without unit";
				this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - ((Unit) product).getProductionCost());
				this.getRulerPlayer().getUnits().add((Unit) product);
				((Unit) product).move(destination);//TODO if using command list, add move command to arraylist
			}else{
				return "not enough money";
			}
		}
		return null;
	}
	private Tile findTileWithNoUnit(Unit unit){
		for (City city : this.getRulerPlayer().getCities()) {
			for (Tile tile : city.getTerritory()) {
				if(tile.getNonCombatUnitInTile() == null && unit instanceof NonCombatUnit) return tile;
				else if(tile.getCombatUnitInTile() == null && unit instanceof CombatUnit) return tile;
			}
		}
		return null;
	}

	public String construct(Construction construction){
		if(inLineConstructionTurn == 0) {
			if(construction instanceof Unit) {
				Tile destination;
				if((destination = (findTileWithNoUnit((Unit) construction))) == null) return"there is no tile without unit";
				this.getRulerPlayer().addUnit((Unit) construction);
				((Unit) construction).move(destination);
			}
			inLineConstructionTurn = 4;
			currentConstruction = null;
			return null;
		}
		if(inLineConstructionTurn < 4) {
			inLineConstructionTurn --;
			return null;
		}
		if(currentConstruction == null && construction != null){
			if(constructionCanBeBuilt(construction)) {
				currentConstruction = construction;
				inLineConstructionTurn--;
				return null;
			}
			return "the construction can't be built";
		}

		return "something else is being constructed or there is nothing to construct";
	}

	private boolean constructionCanBeBuilt(Construction construction){
		if(construction instanceof Unit) {
			if (this.getRulerPlayer().getGold() >= ((Unit) construction).getProductionCost()) {
				this.getRulerPlayer().getUnits().add((Unit) construction);
				this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - ((Unit) construction).getProductionCost());
				return true;
			}
			return false;
		}
		//TODO add building for phase 2;
		return false;
	}

	public String changeConstruction(Construction construction){
		//TODO save previous construction;
		if(currentConstruction == null) return "nothing is being built";
		if(constructionCanBeBuilt(construction)){
			currentConstruction = construction;
			inLineConstructionTurn = 3;
			return null;
		}
		return "cannot change construction";
	}

	public String destroyCity(){
		for (Player player : GameController.getInstance().getPlayers()) {
			if(this == player.getCurrentCapitalCity()) {
				this.state = CityState.NONE;
				return "cannot destroy the capital of a civilization";
			}
		}
		rulerPlayer.getSeizedCities().remove(this);
		this.state = CityState.DESTROYED;
		return null;
	}

	public void attachCity(){
		this.state = CityState.ATTACHED;
	}
	public void seizeCity(Player winner){
		this.rulerPlayer = winner;
		this.state = CityState.SEIZED;
		winner.getSeizedCities().add(this);
	}

	public void updateCityCombatStrength()
	{
		int n = 10;
		this.combatStrength = 10;
		for (Tile tile : this.getTerritory()) {
			if(tile.getCombatUnitInTile() != null && tile.getCombatUnitInTile().getClass().equals(MidRange.class) && tile != this.capitalTile)
				n += tile.getTileType().combatModifier * ((MidRange) tile.getCombatUnitInTile()).getType().combatStrength / 100;
			if(tile.getCombatUnitInTile() != null && tile.getCombatUnitInTile().getClass().equals(LongRange.class) && tile != this.capitalTile)
				n += tile.getTileType().combatModifier * ((LongRange) tile.getCombatUnitInTile()).getType().combatStrength / 100;
		}
		n += (this.getTerritory().size() / 5) * 2; //strength increases 2 degrees for each 5 tiles
		if(this.getCapitalTile().getTileType().equals(TileType.HILLS)) n += 5;
		if(this.getGarrison() != null && this.getGarrison() instanceof LongRange)
			n += ((LongRange) this.getGarrison()).getType().combatStrength;
		else if(this.getGarrison() != null && this.getGarrison() instanceof MidRange)
			n += ((MidRange) this.getGarrison()).getType().combatStrength;
		this.combatStrength = n;
	}

	public String attackCityWithMidRange(City enemy){
		MidRange unit = null;
		if(enemy.getRulerPlayer() == this.getRulerPlayer()) return "cannot attack your city";
		for (Tile tile : this.getTerritory()) {
			if(tile.getCombatUnitInTile() != null && tile.getCombatUnitInTile() instanceof MidRange &&
					((MidRange) tile.getCombatUnitInTile()).getUnitState().equals(UnitState.ACTIVE)){
				unit = (MidRange) tile.getCombatUnitInTile();
				break;
			}
		}
		if(unit == null) return"do not have any melee unit to attack";
		//unit.attack(enemy);
		return null;
	}
	public String attackCityWithLongRange(City enemy){
		LongRange unit = null;
		if(enemy.getRulerPlayer() == this.getRulerPlayer()) return "cannot attack your city";
		for (Tile tile : this.getTerritory()) {
			if(tile.getCombatUnitInTile() != null && tile.getCombatUnitInTile() instanceof LongRange &&
					((LongRange) tile.getCombatUnitInTile()).getUnitState().equals(UnitState.ACTIVE)){
				unit = (LongRange) tile.getCombatUnitInTile();
				break;
			}
		}
		if(unit == null) return"do not have any melee unit to attack";
		//unit.attack(enemy);
		return null;
	}

}




















