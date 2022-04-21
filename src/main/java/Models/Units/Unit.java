package Models.Units;

import Models.Game.Position;
import Models.Player.Technology;
import Models.Resources.Resource;

import java.util.ArrayList;

public abstract class Unit
{
	private int productionCost;
	private int movementPoints;
	private Position position;
	private int health;
	private int speed;
	private int power;
	private Technology requiredTechnology; //TODO
	private Resource requiredResource;
	private ArrayList<Position> moves;
	private ArrayList commands;
	private boolean isActive;
	private boolean isSleep;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
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

	private void sleep(){

	}
	private void getReady(){

	}
	private void reinforce(){

	}
	private void reinforceTillRecovery(){

	}
	private void getSet(){

	}
	private void cancelCommand(){

	}
	private void awaken(){

	}
	private void removeUnit(){

	}
	
}


































