package Models.Units;

import Models.Game.Position;
import Models.Resources.Resource;
import Models.Player.Technology;

import java.util.ArrayList;

public abstract class Unit
{
	private int productionCost;
	private int movementPoints;
	private Position position;
	private int health;
	private Technology requiredTechnology; //TODO
	private Resource requiredResource;
	private ArrayList<Position> moves;
	private boolean isActive;
	private boolean isSleep;
	
}


































