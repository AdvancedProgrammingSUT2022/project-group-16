package Models.Units;

import Models.City.Constructable;
import Models.Game.Position;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.Resource;
import Models.Terrain.Tile;

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
	private ArrayList<Position> moves;
	private ArrayList commands;
	private boolean isActive;
	private boolean isSleep;
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

	public void setCommands(ArrayList commands) {
		this.commands = commands;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isSleep() {
		return isSleep;
	}

	public void setSleep(boolean sleep) {
		isSleep = sleep;
	}

	public void sleep(){
		this.isActive = false;
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
		this.tile = destination;
		this.movementPoints -= destination.getTileType().movementCost;
		if(this.movementPoints < 0) this.movementPoints = 0;
		//TODO include railroad movement points
		//TODO write a unit test for findWay
		moves = FindWay.getInstance(destination).getMoves();
		//TODO check if unit can go to destination
	}
	
}


































