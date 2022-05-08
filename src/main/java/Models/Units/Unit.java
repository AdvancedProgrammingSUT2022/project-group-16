package Models.Units;

import Models.City.Constructable;
import Models.Game.Position;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.Resource;
import Models.Terrain.BorderType;
import Models.Terrain.Tile;
import Models.Terrain.TileFeature;
import Models.Terrain.TileType;
import Models.Units.CombatUnits.CombatUnit;
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
	private ArrayList commands = new ArrayList<>();
	private boolean isActive = true;
	private boolean isSleep = false;
	private boolean hasArrived = false;



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
	public boolean isHasArrived() {
		return hasArrived;
	}

	public void setHasArrived(boolean hasArrived) {
		this.hasArrived = hasArrived;
	}

	public ArrayList getCommands() {
		return commands;
	}

	public void addCommands(String command) {
		commands.add(command);
	}    //TODO what is the command Type?

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
	public void reinforce(){

	}
	public void reinforceTillRecovery(){

	}
	public void getSet(){

	}
	public void cancelCommand(int i){
		commands.remove(i);
	}
	public void awaken(){
		isSleep = false;
	}
	public void removeUnit(){
		rulerPlayer.setGold((int) (rulerPlayer.getGold() + (this.productionCost * 0.1)));
		rulerPlayer.getUnits().remove(this);
	}

	public void move(Tile destination){

		this.moves = FindWay.getInstance(destination).getMoves();
		if(this.moves.size() == 0) return;
		Tile nextTile = rulerPlayer.getTileByXY(moves.get(0).X, moves.get(0).Y);
		if((nextTile.getTileType().equals(TileType.OCEAN) && (!nextTile.getHasRoad() || !this.getTile().getHasRoad())) ||
				(this.moves.size() == 1 && isThereAnotherUnitInTile(nextTile))){
			return;
		}
		this.tile = this.getRulerPlayer().getTileByXY(this.moves.get(0).X, this.moves.get(0).Y);
		this.moves.remove(0);

		if(this.movementPoints == 0) {
			return;
		}
		this.movementPoints -= destination.getTileType().movementCost;
		if(nextTile.getBorders().equals(BorderType.RIVER) && (!nextTile.getHasRoad() || !this.getTile().getHasRoad())) this.movementPoints = 0;
			if(this.movementPoints < 0) this.movementPoints = 0;
		//TODO check for railroad penalty



	}
	private boolean isThereAnotherUnitInTile(Tile tile){
		for (Unit unit : rulerPlayer.getUnits()) {
			if(unit.getTile().getPosition().X == tile.getPosition().X &&
					unit.getTile().getPosition().Y == tile.getPosition().Y &&
					((unit instanceof NonCombatUnit && this instanceof NonCombatUnit) ||
							(unit instanceof CombatUnit && this instanceof CombatUnit)))
				return true;
		}
		return false;
	}
	
}


































