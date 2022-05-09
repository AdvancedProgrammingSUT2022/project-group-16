package Models.Terrain;

import Models.Game.Position;
import Models.Resources.Resource;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;

public class Tile
{
	private final Position position;
	private final TileType tileType;
	private final TileFeature tileFeature;
	// 6 borders, starting from the north border from 0. (counterclockwise)
	private BorderType[] borders;
	private Resource resource;
	private Improvement improvement = Improvement.NONE;
	private boolean hasRoad;
	private boolean hasRailRoad;
	private CombatUnit combatUnitInTile = null;
	private NonCombatUnit nonCombatUnitInTile = null;
	boolean isPillaged = false;
	boolean isRuined; //not sure if it should be boolean
	
	public Tile(Position position, TileType tileType, TileFeature tileFeature, BorderType[] borders, Resource resource)
	{
		this.position = position;
		this.tileType = tileType;
		this.tileFeature = tileFeature;
		this.borders = borders;
		this.resource = resource;
	}
	
	public TileType getTileType()
	{
		return tileType;
	}
	public TileFeature getTileFeature()
	{
		return tileFeature;
	}
	public BorderType[] getBorders()
	{
		return borders;
	}
	public void setBorders(BorderType[] borders)
	{
		this.borders = borders;
	}
	public Resource getResource()
	{
		return resource;
	}
	public Improvement getImprovement()
	{
		return improvement;
	}
	public void setImprovement(Improvement improvement)
	{
		this.improvement = improvement;
	}
	public boolean hasRoad()
	{
		return hasRoad;
	}
	public void setHasRoad(boolean hasRoad)
	{
		this.hasRoad = hasRoad;
	}
	public boolean hasRailRoad()
	{
		return hasRailRoad;
	}
	public void setHasRailRoad(boolean hasRailRoad)
	{
		this.hasRailRoad = hasRailRoad;
	}
	public CombatUnit getCombatUnitInTile()
	{
		return combatUnitInTile;
	}
	public void setCombatUnitInTile(CombatUnit combatUnit)
	{
		this.combatUnitInTile = combatUnit;
	}
	public NonCombatUnit getNonCombatUnitInTile()
	{
		return nonCombatUnitInTile;
	}
	public void setNonCombatUnitInTile(NonCombatUnit nonCombatUnit)
	{
		this.nonCombatUnitInTile = nonCombatUnit;
	}
	public boolean isPillaged()
	{
		return isPillaged;
	}
	public void setIsPillaged(boolean isPillaged)
	{
		this.isPillaged = isPillaged;
	}
	public Position getPosition()
	{
		return position;
	}
	protected Tile clone()
	{ //TODO: assert that this is a deep copy and everything is cloned correctly
		Tile newTile = new Tile(position, tileType, tileFeature, null, resource);
		BorderType[] newBorders = new BorderType[6];
		for (int i = 0; i < 6; i++)
			newBorders[i] = borders[i];
		newTile.borders = newBorders;
		newTile.hasRoad = hasRoad;
		newTile.hasRailRoad = hasRailRoad;
		newTile.combatUnitInTile = combatUnitInTile.clone();
		newTile.combatUnitInTile.setTile(newTile);
		newTile.nonCombatUnitInTile = nonCombatUnitInTile.clone();
		newTile.nonCombatUnitInTile.setTile(newTile);
		newTile.isPillaged = isPillaged;
		newTile.isRuined = isRuined;
		return newTile;
	}
}















