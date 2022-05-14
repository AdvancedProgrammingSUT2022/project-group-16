package Models.Units;

import Models.City.City;
import Models.City.Construction;
import Models.Terrain.Position;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.Resource;
import Models.Terrain.BorderType;
import Models.Terrain.Tile;
import Models.Terrain.TileType;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.MidRange;
import Models.Units.CommandHandeling.UnitCommands;
import Models.Units.NonCombatUnits.NonCombatUnit;
import enums.gameCommands.unitCommands;

import java.util.ArrayList;

public abstract class Unit implements Construction
{
	private Player rulerPlayer;
	private int productionCost;
	private int movementPoints;
	private Tile tile;
	public final int MAX_HEALTH = 10;
	private int health = MAX_HEALTH;
	private int speed;
	private int power;
	private Technology requiredTechnology; //TODO
	private Resource requiredResource;
	private ArrayList<Position> moves = new ArrayList<>();
	private ArrayList<UnitCommands> commands = new ArrayList<>();
	private boolean hasArrived = false;
	private UnitState unitState = UnitState.ACTIVE;
	private Tile destination = null;


	public Player getRulerPlayer() {
		return rulerPlayer;
	}

	public void setRulerPlayer(Player rulerPlayer) {
		this.rulerPlayer = rulerPlayer;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public UnitState getUnitState() {
		return unitState;
	}
	public void setUnitState(UnitState unitState) {
		this.unitState = unitState;
	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getProductionCost() {
		return productionCost;
	}

	public void setProductionCost(int productionCost) {
		this.productionCost = productionCost;
	}

	public int getMovementPoints() {
		return movementPoints;
	}

	public void setMovementPoints(int movementPoints) {
		this.movementPoints = movementPoints;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public Technology getRequiredTechnology() {
		return requiredTechnology;
	}

	public void setRequiredTechnology(Technology requiredTechnology) {
		this.requiredTechnology = requiredTechnology;
	}

	public Resource getRequiredResource() {
		return requiredResource;
	}

	public void setRequiredResource(Resource requiredResource) {
		this.requiredResource = requiredResource;
	}

	public ArrayList<Position> getMoves() {
		return moves;
	}

	public void setMoves(ArrayList<Position> moves) {
		this.moves = moves;
	}
	public boolean isHasArrived() {
		return hasArrived;
	}

	public void setHasArrived(boolean hasArrived) {
		this.hasArrived = hasArrived;
	}

	public ArrayList<UnitCommands> getCommands() {
		return commands;
	}

	public void addCommand(UnitCommands command) {
		commands.add(command);
	}
	public Tile getDestination() {
		return destination;
	}

	public void setDestination(Tile destination) {
		this.destination = destination;
	}

	public void getReady(){

	}
	public void fortify(){

	}
	public void fortifyTillHeel(){

	}
	public void getSet(){

	}
	public String attackToCity(City city)
	{
		if(city.getHitPoints() == 1 && this.getClass().equals(MidRange.class))
		{
			//seized city
			rulerPlayer.getSelectedUnit().setTile(city.getCapitalTile());
			city.seizeCity(this.rulerPlayer);
			return unitCommands.citySeized.regex;
		}
		else if(city.getHitPoints() == 1)
			return unitCommands.longRangeSeizedCity.regex;
		else if(this.getClass().equals(MidRange.class)) //midrange attack
		{
			int y = ((MidRange) this).getType().getCombatStrength();
			double x = (double) (10 + this.getHealth()) / 20;
			int cityDamage = (city.getCombatStrength() - (int) (x * y));
			int unitDamage = ((((MidRange) this).getType().getCombatStrength()) - city.getCombatStrength());
			rulerPlayer.setXP(rulerPlayer.getXP() + 1);
			city.getRulerPlayer().setXP(rulerPlayer.getXP() + 1);
			if(cityDamage < 0)
			{
				city.setHitPoints(city.getHitPoints() + cityDamage);
				if(city.getHitPoints() < 1)
				{
					rulerPlayer.getSelectedUnit().setTile(city.getCapitalTile());
					city.seizeCity(rulerPlayer);
					return null;
				}
			}
			if(unitDamage < 0)
			{
				this.setHealth(this.getHealth() + unitDamage);
				if(this.getHealth() <= 0)
				{
					removeUnit();
					return unitCommands.unitDestroy.regex;
				}
			}
		}
		else //long range attack
		{
			int cityDamage = (city.getCombatStrength() - (((LongRange) this).getType().getCombatStrength() * ((10 - this.getHealth()) / 20)));
			rulerPlayer.setXP(rulerPlayer.getXP() + 1);
			city.getRulerPlayer().setXP(rulerPlayer.getXP() + 1);
			if(cityDamage < 0)
			{
				city.setHitPoints(city.getHitPoints() + cityDamage);
				if(city.getHitPoints() < 1) {
					city.setHitPoints(1);
					return unitCommands.longRangeSeizedCity.regex;
				}
			}
		}
		return unitCommands.successfullAttack.regex;
	}
	public void cancelCommand(int i){
		commands.remove(i);
	}
	public void removeUnit(){
		rulerPlayer.getUnits().remove(this);
	}

	public String move(Tile destination){
		if(destination == null) return "no destination";
		this.destination = destination;
		FindWay.getInstance().calculateShortestWay(this.tile.getPosition(), destination.getPosition());
		this.moves = FindWay.getInstance().getMoves();
		return updateUnitMovements();
	}
	public String updateUnitMovements(){
		if(this.getMovementPoints() == 0) return "no movementPoints";
		if(this.moves.size() == 0 && !this.getTile().equals(this.destination)) return "cannot move to destination";
		if(this.moves.size() == 0 && this.getTile().equals(this.destination)){
			if(!isTileEnemy(this.destination)){
				if(this instanceof CombatUnit) this.getTile().setCombatUnitInTile((CombatUnit) this);
				else if(this instanceof NonCombatUnit) this.getTile().setNonCombatUnitInTile((NonCombatUnit) this);
			}
			this.destination = null;
		}
		Tile nextTile = this.getRulerPlayer().getTileByXY(this.moves.get(0).X, this.moves.get(0).Y);
		if(this.movementPoints < nextTile.getTileType().movementCost && !canUnitStayInTile(nextTile))
			return"cannot stay in destination Tile";
		if(this instanceof CombatUnit && this.getTile().getCombatUnitInTile() == this)
			this.getTile().setCombatUnitInTile(null);
		else if(this instanceof NonCombatUnit && this.getTile().getNonCombatUnitInTile() == this)
			this.getTile().setNonCombatUnitInTile(null);
		this.setTile(nextTile);
		this.getMoves().remove(0);
		this.movementPoints -= destination.getTileType().movementCost;
		//if(nextTile.getBorders()[0].equals(BorderType.RIVER) && (!nextTile.hasRoad() || !this.getTile().hasRoad())) this.movementPoints = 0;
		if(this.movementPoints < 0) this.movementPoints = 0;
		//TODO check for railroad penalty
		return null;
	}
	private boolean canUnitStayInTile(Tile destination){
		if((destination.getTileType().equals(TileType.OCEAN) && (!destination.hasRoad() || !this.getTile().hasRoad()))){
			return false;
		}
		if(this.moves.size() == 1 && isThereAnotherUnitInTile(destination) && !isTileEnemy(destination))
			return false;
		return true;
	}
	private boolean isTileEnemy(Tile destination){
		for (City city : this.getRulerPlayer().getCities()) {
			if(city.getTerritory().contains(destination)) return false;
		}
		return true;
	}
	public boolean isThereAnotherUnitInTile(Tile tile){
		if(tile.getCombatUnitInTile() != null && (this instanceof CombatUnit)) return true;
		if(tile.getNonCombatUnitInTile() != null && (this instanceof NonCombatUnit)) return true;
		return false;
	}
	
	public abstract Unit clone();
}


































