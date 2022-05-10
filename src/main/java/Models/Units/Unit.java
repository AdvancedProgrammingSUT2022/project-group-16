package Models.Units;

import Models.City.Constructable;
import Models.Game.Position;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.Resource;
import Models.Terrain.BorderType;
import Models.Terrain.Tile;
import Models.Terrain.TileType;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CommandHandeling.UnitCommands;
import Models.Units.NonCombatUnits.NonCombatUnit;

import java.util.ArrayList;

public abstract class Unit implements Constructable
{
	private Player rulerPlayer;
	private int productionCost;
	private int movementPoints;
	private Tile tile;
	private int health;
	private int speed;
	private int power;
	private Technology requiredTechnology; //TODO
	private Resource requiredResource;
	private ArrayList<Position> moves = new ArrayList<>();
	private ArrayList<UnitCommands> commands = new ArrayList<>();
	private boolean isActive = true;
	private boolean isSleep = false;



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

	public ArrayList<UnitCommands> getCommands() {
		return commands;
	}

	public void addCommand(UnitCommands command) {
		commands.add(command);
	}

	public boolean isActive() {
		return isActive;
	}

	public void changeActivate()
	{
		isActive = !isActive;
	}

	public boolean isSleep() {
		return isSleep;
	}

	public void changeSleepWake()
	{
		isSleep = !isSleep;
	}

	public void getReady(){

	}
	public void fortify(){

	}
	public void fortifyTillHeel(){

	}
	public void getSet(){

	}
	public void cancelCommand(int i){
		commands.remove(i);
	}
	public void awaken(){
		isSleep = !isSleep;
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
			this.commands.remove(0);
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


































