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
	private Improvement improvement;
	private boolean hasRoad;
	private boolean hasRailRoad;
	private CombatUnit combatUnitInTile;
	private NonCombatUnit nonCombatUnitInTile;
	boolean isPillaged;
	
	public Tile(Position position, TileType tileType, TileFeature tileFeature, BorderType[] borders, Resource resource)
	{
		this.position = position;
		this.tileType = tileType;
		this.tileFeature = tileFeature;
		this.borders = borders;
		this.resource = resource;
		this.combatUnitInTile = null;
		this.nonCombatUnitInTile = null;
		this.improvement = Improvement.NONE;
		this.isPillaged = false;
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
	public boolean getHasRoad()
	{
		return hasRoad;
	}
	public void setHasRoad(boolean hasRoad)
	{
		this.hasRoad = hasRoad;
	}
	public boolean getHasRailRoad()
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
	public NonCombatUnit getNonCombatUnitInTile()
	{
		return nonCombatUnitInTile;
	}
	public boolean isPillaged()
	{
		return isPillaged;
	}
	public Position getPosition()
	{
		return position;
	}
	// TODO: override equals???
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
















