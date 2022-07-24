package Models.City;

import Controllers.GameController;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Player.TileState;
import Models.Resources.Resource;
import Models.Terrain.Tile;
import Models.Terrain.TileType;
import Models.Units.CombatUnits.*;
import Models.Units.NonCombatUnits.*;
import Models.Units.Unit;
import Models.Units.UnitState;
import com.sun.nio.sctp.Notification;
import enums.gameEnum;
import enums.mainCommands;

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
	private CombatUnit garrison = null;
	private NonCombatUnit nonCombatUnit = null;
	private int combatStrength = 10;//this amount is default and may change later
	private int longRangeCombatStrength; // range is 2;
	transient private Player rulerPlayer;
	private final String name;
	private CityState state = CityState.NONE;
	private final GameController gameController = GameController.getInstance();

	public City(Tile capitalTile, Player rulerPlayer)
	{
		//TODO: check that we can't build a city in distance of 1 cities
		this.capitalTile = capitalTile;
		this.rulerPlayer = rulerPlayer;
		territory.add(capitalTile);
		territory.addAll(rulerPlayer.getAdjacentTiles(capitalTile, 1));
		this.name = setCityName();
		rulerPlayer.addCity(this);
		if(rulerPlayer.getCities().size() == 1)
		{
			rulerPlayer.setHasCity(true);
			cupYield = 3;
			rulerPlayer.setCup(3);
			rulerPlayer.setCurrentCapitalCity(this);
		}
		this.combatStrength += this.territory.size();
		this.longRangeCombatStrength = combatStrength;
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

	public void repairCity(){
		for (Tile tile : territory) {
			if(tile.isRuined() && tile.getNonCombatUnitInTile() != null && tile.getNonCombatUnitInTile() instanceof Worker){
				this.hitPoints --;
				((Worker) tile.getNonCombatUnitInTile()).repairTile();
			}
		}
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
	public void setCurrentConstruction(Construction currentConstruction)
	{
		this.currentConstruction = currentConstruction;
	}
	public int getHitPoints() {
		return hitPoints;
	}
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}

	public int getFoodYield()
	{
		int gainingFood = 0;
		int consumingFood = 0;
		for(Citizen citizen : citizens)
		{
			consumingFood++;
			if(citizen.getWorkingTile() == null)
				continue;
			Tile workingTile = citizen.getWorkingTile();
			gainingFood += workingTile.getTileType().food + workingTile.getTileFeature().food + workingTile.getImprovement().foodYield;
			if(workingTile.getResource() == null)
				continue;
			if(workingTile.getImprovement().equals(workingTile.getResource().getRESOURCE_TYPE().requiredImprovement))
				gainingFood += workingTile.getResource().getRESOURCE_TYPE().food;
		}
		for(Unit unit : rulerPlayer.getUnits())
			if(unit instanceof Settler)
				consumingFood += 2;

		return gainingFood - consumingFood;
	}
	public void setFoodYield(int foodYield) {
		this.foodYield = foodYield;
	}

	public void setGoldYield(int goldYield) {
		this.goldYield = goldYield;
	}

	public int getGoldYield()
	{
		int gainingGold = goldYield;
		int goldConsumption = 0;
		for(Citizen citizen : citizens)
		{
			if(citizen.getWorkingTile() == null)
				continue;
			Tile workingTile = citizen.getWorkingTile();
			gainingGold += workingTile.getTileType().gold + workingTile.getTileFeature().gold + workingTile.getImprovement().goldYield;

			if(workingTile.getResource() == null)
				continue;

			if(workingTile.getImprovement().equals(workingTile.getResource().getRESOURCE_TYPE().requiredImprovement))
				gainingGold += workingTile.getResource().getRESOURCE_TYPE().gold;
		}
		for(Unit unit : rulerPlayer.getUnits())
			if(unit instanceof CombatUnit)
				goldConsumption += 1;

		return gainingGold - goldConsumption;
	}
	public int getProductionYield() {
		return productionYield;
	}

	public void setProductionYield(int productionYield) {
		this.productionYield = productionYield;
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
	public int getPopulation()
	{
		return citizens.size();
	}

	public void addPopulation(int amount)
	{
		for(int i = 0; i < amount; i++){
			citizens.add(new Citizen(this));
		}
		rulerPlayer.setScore(rulerPlayer.getScore() + amount);
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
		garrison.setDefencePower((int) (garrison.getDefencePower() * 1.5));
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
	public void setCapitalTile(Tile capitalTile)
	{
		this.capitalTile = capitalTile;
	}

	public String purchaseTile(Tile tile){
		for (Player player : gameController.getPlayers())
			for (City city : player.getCities())
				if (city.getTerritory().contains(tile))
					return gameEnum.belongToCivilization.regex;
		if(getRulerPlayer().getGold() < getRulerPlayer().getTilePurchaseCost())
			return gameEnum.notEnoughGold.regex;
		else if (gameController.getPlayerTurn().getMap().get(tile).equals(TileState.FOG_OF_WAR))
			return gameEnum.fogOfWar.regex;
		else if(isTileNeighbor(tile)) {
			this.territory.add(tile);
			this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - getRulerPlayer().getTilePurchaseCost());
			this.getRulerPlayer().setTilePurchaseCost((int) (1.2 * getRulerPlayer().getTilePurchaseCost()));
			return gameEnum.buyTile.regex;
		}
		return gameEnum.cantBuyTile.regex;
	}
	private boolean isTileNeighbor(Tile newTile) {
		if(territory.contains(newTile))
			return false;
		for (Tile tile : territory) {
			if(tile.distanceTo(newTile) == 1)
				return true;
		}
		return false;
	}

	private int getCost(Unit unit)
	{
		if(unit instanceof MidRange) return ((MidRange) unit).getType().getCost();
		if(unit instanceof LongRange) return ((LongRange) unit).getType().getCost();
		if(unit.getClass().equals(Settler.class)) return 89;
		if(unit.getClass().equals(Worker.class)) return 70;
		return 0;
	}
	public Tile findTileWithNoCUnit(){
		for (City city : this.getRulerPlayer().getCities())
			for (Tile tile : city.getTerritory())
				if(tile.getCombatUnitInTile() == null) return tile;
		return null;
	}
	public Tile findTileWithNoNCUnit(){
		for (City city : this.getRulerPlayer().getCities())
			for (Tile tile : city.getTerritory())
				if(tile.getNonCombatUnitInTile() == null) return tile;
		return null;
	}

	private Tile findTileWithNoUnit(Unit unit)
	{
		for (City city : this.getRulerPlayer().getCities())
			for (Tile tile : city.getTerritory()) {
				if (tile.getCombatUnitInTile() != null && unit instanceof CombatUnit) return tile;
				else if(tile.getNonCombatUnitInTile() != null && unit instanceof NonCombatUnit) return tile;
			}
		return null;
	}
	public String destroyCity(){
//		for (Player player : GameController.getPlayers()) {
//			if(this == player.getCurrentCapitalCity()) {
//				this.state = CityState.NONE;
//				return "cannot destroy the capital of a civilization";
//			}
//		}
		rulerPlayer.getSeizedCities().remove(this);
		this.state = CityState.DESTROYED;
		return null;
	}

	public void attachCity(){
		this.state = CityState.ATTACHED;
	}
	public void seizeCity(Player winner){
		clearMilitaryBuildings();
		this.rulerPlayer = winner;
		this.state = CityState.SEIZED;
		winner.getSeizedCities().add(this);
	}

	private void clearMilitaryBuildings() {
		for (int i = 0; i < this.buildings.size(); i++) {
			if(buildings.get(i).getBuildingType().equals(BuildingType.BARRACKS) ||
					buildings.get(i).getBuildingType().equals(BuildingType.ARMORY)||
					buildings.get(i).getBuildingType().equals(BuildingType.TEMPLE) ||
					buildings.get(i).getBuildingType().equals(BuildingType.MONASTERY) ||
					buildings.get(i).getBuildingType().equals(BuildingType.MILITARY_ACADEMY) ||
					buildings.get(i).getBuildingType().equals(BuildingType.ARSENAL) ||
					buildings.get(i).getBuildingType().equals(BuildingType.MILITARY_BASE)){
				this.buildings.remove(i);
			}
		}
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
	private String constructionCanBeBuilt(Construction construction){
		if(construction instanceof Unit) {
			if (this.getRulerPlayer().getGold() >= ((Unit) construction).getProductionCost()) {
				this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - ((Unit) construction).getProductionCost());
				return "built successfully";
			}
			return gameEnum.notEnoughGold.regex;
		}

		else if(construction instanceof Building){
			if(rulerPlayer.getGold() >= ((Building) construction).getBuildingType().cost) {
				this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - ((Building) construction).getBuildingType().cost);
				return "built successfully";
			}
			return gameEnum.notEnoughGold.regex;
		}
		return null;
	}


//	private String buildingHasRequirements(Construction construction){
//		boolean hasBuilding = false;
//		if (((Building)construction).getBuildingType().requiredBuilding == null) hasBuilding = true;
//		for (City city : this.getRulerPlayer().getCities()) {
//			for (Building building : city.getBuildings()) {
//				if(building.getBuildingType().equals(((Building) construction).getBuildingType().requiredBuilding)){
//					hasBuilding = true;
//					break;
//				}
//			}
//		}
//		if(!hasBuilding) return "do not have required building";
//		return null;
//	}

	public Tile getTileWithNoBuilding() {
		for (Tile tile : territory) {
			boolean isEmpty = true;
			for (Building building : buildings) {
				if(building.getTile().getPosition().equals(tile.getPosition())){
					isEmpty = false;
					break ;
				}
			}
			if(isEmpty) return tile;
		}
		return null;
	}

	public String construct(Construction construction, GameController gameController)
	{
		if(currentConstruction == null) {
			if(!constructionCanBeBuilt(construction).equals("built successfully"))
				return "cannot build";
			currentConstruction = construction;
			construction.setTurnTillBuild(4);
			rulerPlayer.setScore(rulerPlayer.getScore() + 2 * gameController.MAP_SIZE);
			return gameEnum.successfulBuild.regex;
		}
		if(construction.getTurnTillBuild() == 0)
		{
			Tile destination;
			try {
				MidRangeType midRangeType = MidRangeType.valueOf(currentConstruction.toString());
				if((destination = findTileWithNoCUnit()) == null)
					return "no tile empty";
				new MidRange(rulerPlayer, midRangeType, destination);
				construction.setTurnTillBuild(4);
				currentConstruction = null;
				return null;
			}catch (IllegalArgumentException e){
			}
			try {
				LongRangeType longRangeType = LongRangeType.valueOf(currentConstruction.toString());
				if((destination = findTileWithNoCUnit()) == null)
					return "no tile empty";
				new LongRange(rulerPlayer, longRangeType, destination);
				construction.setTurnTillBuild(4);
				currentConstruction = null;
				return null;
			}catch (IllegalArgumentException e){

			}
			if(currentConstruction.toString().equals("SETTLER")) {
				if((destination = findTileWithNoCUnit()) == null)
					return "no tile empty";
				new Settler(rulerPlayer, destination);
			}
			else if(currentConstruction.toString().equals("WORKER")) {
				if((destination = findTileWithNoCUnit()) == null)
					return "no tile empty";
				new Worker(rulerPlayer, destination);
			}
			else if(currentConstruction.toString().equals("Building")){
				createBuilding((Building) currentConstruction);
			}
			construction.setTurnTillBuild(4);
			currentConstruction = null;
			return null;
		}
		if(construction.getTurnTillBuild() <= 4 && currentConstruction != null) {
			construction.setTurnTillBuild(construction.getTurnTillBuild() - 1);
			return null;
		}
		return "something else is being constructed or there is nothing to construct";
	}

	private void createBuilding(Building building)
	{
		building.setCity(this);
		buildings.add(building);
		rulerPlayer.setHappiness(rulerPlayer.getHappiness() + building.happinessFromBuilding(building.getBuildingType())); //Increase happiness
	}


	public String changeConstruction(Construction construction){
		if(constructionCanBeBuilt(construction).equals("built successfully")){
			currentConstruction = construction;
			currentConstruction.setTurnTillBuild(3);
			return null;
		}
		return "cannot change construction";
	}

	public String buyBuilding(Building building){
		this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - building.getBuildingType().cost);
		buildings.add(building);
		building.setCity(this);
		return mainCommands.buildingBuilt.regex;
	}

	public String buyUnit(Unit unit){
		this.getRulerPlayer().setGold(this.getRulerPlayer().getGold() - getCost(unit));
		return gameEnum.unitBought.regex;
	}

	public boolean doesCityHaveBuilding(BuildingType buildingType) {
		for (Building building : buildings) {
			if(building.getBuildingType().equals(buildingType))
				return true;
		}
		return false;
	}

}




















