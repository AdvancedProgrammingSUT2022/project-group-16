package Models.Units;

import Models.City.City;
import Models.City.Construction;
import Models.Game.Position;
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
			if(cityDamage < 0)
			{
				city.setHitPoints(city.getHitPoints() + cityDamage);
				if(city.getHitPoints() < 1) {
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
		if(this.getTile().getPosition().equals(destination.getPosition())) return "the unit is in destination Tile";
		this.moves = FindWay.getInstance(destination).getMoves();
		if(this.moves.size() == 0) return "the unit can't go to destination";
		Tile nextTile = rulerPlayer.getTileByXY(moves.get(0).X, moves.get(0).Y);
		if((nextTile.getTileType().equals(TileType.OCEAN) && (!nextTile.hasRoad() || !this.getTile().hasRoad()))){
			return "the unit can't go to destination";
		}
		if(this.moves.size() == 1 && isThereAnotherUnitInTile(nextTile)) return "there is another unit of this type at destination";
		if(this.movementPoints == 0) {
			return "the unit doesn't have any MPs";
		}
		if(this instanceof CombatUnit && this.getTile().getCombatUnitInTile() == this) this.getTile().setCombatUnitInTile(null);
		if(this instanceof NonCombatUnit && this.getTile().getNonCombatUnitInTile() == this) this.getTile().setNonCombatUnitInTile(null);
			this.tile = nextTile;
		this.moves.remove(0);
		if(this.moves.size() == 0){
			if(this instanceof CombatUnit) this.getTile().setCombatUnitInTile((CombatUnit) this);
			if(this instanceof NonCombatUnit) this.getTile().setNonCombatUnitInTile((NonCombatUnit) this);
			//this.commands.remove(0);
		}

		this.movementPoints -= destination.getTileType().movementCost;
		if(nextTile.getBorders().equals(BorderType.RIVER) && (!nextTile.hasRoad() || !this.getTile().hasRoad())) this.movementPoints = 0;
			if(this.movementPoints < 0) this.movementPoints = 0;
		//TODO check for railroad penalty
		return null;
	}
	private boolean isThereAnotherUnitInTile(Tile tile){
		if(tile.getCombatUnitInTile() != null && (this instanceof CombatUnit)) return true;
		if(tile.getNonCombatUnitInTile() != null && (this instanceof NonCombatUnit)) return true;
		return false;
	}
	
	public abstract Unit clone();
}


































