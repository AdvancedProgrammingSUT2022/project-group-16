package Models.Units;

import Controllers.GameController;
import Models.City.City;
import Models.City.Construction;
import Models.Player.Notification;
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

public abstract class Unit extends Construction
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
	private int XP = 10;


	public String toString(){
		return "Unit";
	}

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

	public int getXP() {
		return XP;
	}

	public void setXP(int XP) {
		this.XP = XP;
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
	public boolean HasArrived() {
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

	public void setAsleep(){
		this.unitState = UnitState.SLEEPING;
	}
	public String awaken(){
		if(!this.unitState.equals(UnitState.SLEEPING)) return "unit is not asleep";
		this.unitState = UnitState.ACTIVE;
		return null;
	}

	public void setAlert(){
		if(isThereEnemyUnitNear()){
			unitState = UnitState.ACTIVE;
			return;
		}
		unitState = UnitState.ALERT;
	}

	private boolean isThereEnemyUnitNear() {
		for (Tile tile1 : GameController.getInstance().getMap()) {
			if(this.getTile().distanceTo(tile1) == 1){
				if(tile1.getCombatUnitInTile() != null && tile1.getCombatUnitInTile().getRulerPlayer() != this.rulerPlayer)
					return true;
			}
		}
		return false;
	}

	public void cancelCommand(int i){
		commands.remove(i);
	}

	public String move(Tile destination){
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
			return null;
		}
		Tile nextTile;
		nextTile = this.getRulerPlayer().getTileByXY(this.moves.get(0).X, this.moves.get(0).Y);
		if (this.movementPoints < nextTile.getTileType().movementCost && !canUnitStayInTile(nextTile))
			return "cannot stay in destination Tile";
		if (this instanceof CombatUnit && this.getTile().getCombatUnitInTile() == this)
			this.getTile().setCombatUnitInTile(null);
		else if (this instanceof NonCombatUnit && this.getTile().getNonCombatUnitInTile() == this)
			this.getTile().setNonCombatUnitInTile(null);
		//this.movementPoints -= destination.getTileType().movementCost;
		if(nextTile.getBoarderType(this.getTile()).equals(BorderType.RIVER) && (!nextTile.hasRoad() || !this.getTile().hasRoad())) this.movementPoints = 0;
		//if (this.movementPoints < 0) this.movementPoints = 0;
		//TODO check for railroad penalty
		this.setTile(nextTile);
		this.getMoves().remove(0);
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
		for (Player player : GameController.getInstance().getPlayers()) {
			for (City city : player.getCities()) {
				for (Tile tile : city.getTerritory()) {
					if(destination.getPosition().equals(tile.getPosition()) && player != this.getRulerPlayer())
						return true;
				}
			}
		}
		return false;
	}
	public boolean isThereAnotherUnitInTile(Tile tile){
		if(tile.getCombatUnitInTile() != null && (this instanceof CombatUnit)) return true;
		if(tile.getNonCombatUnitInTile() != null && (this instanceof NonCombatUnit)) return true;
		return false;
	}

	public void destroy(){
		this.getRulerPlayer().setGold((int) (this.rulerPlayer.getGold() + (0.1 * this.productionCost)));
		rulerPlayer.getUnits().remove(this);
		if(this instanceof NonCombatUnit) this.getTile().setNonCombatUnitInTile(null);
		else if(this instanceof CombatUnit) this.getTile().setCombatUnitInTile(null);

	}

	public abstract Unit clone();
}


































