package Models.Terrain;

import Models.Game.Position;
import Models.Resources.Resource;
public class Tile
{
	private Position position;
	private TileType tileType;
	private TileFeature tileFeature;
	private BorderType[] borders;
	private Resource resource;
	private Improvement improvement;
	//	CombatUnit combatUnitInTile; TODO
	//	NonCombatUnit combatUnitInTile; TODO
	boolean isPillaged;
	
	public Tile(Position position, TileType tileType, TileFeature tileFeature, BorderType[] borders, Resource resource)
	{
		this.position = position;
		this.tileType = tileType;
		this.tileFeature = tileFeature;
		this.borders = borders;
		this.resource = resource;
		improvement = null;
		isPillaged = false;
	}
	
}
















