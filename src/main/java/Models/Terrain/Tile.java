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
	public Position getPosition()
	{
		return position;
	}
	public void improveTile(){

	}
	// TODO: override equals???
	protected Tile clone()
	{
//		return new Tile(position, tileType, tileFeature, borders, resource, improvement, combatUnitInTile, nonCombatUnitInTile);
	}
}















