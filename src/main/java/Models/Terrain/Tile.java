package Models.Terrain;

import Controllers.GameController;
import Models.City.City;
import Models.Player.Player;
import Models.Resources.Resource;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;

public class Tile
{
	private final Position position;
	private TileType tileType;
	private  TileFeature tileFeature;

	// 6 borders, starting from the north border from 0. (counterclockwise)
	private BorderType[] borders;
	private Resource resource;
	private Improvement improvement = Improvement.NONE;
	private boolean hasRoad;
	private boolean hasRailRoad;
	private CombatUnit combatUnitInTile = null;
	private NonCombatUnit nonCombatUnitInTile = null;
	boolean isPillaged = false;
	boolean isRuined = false;


	public Tile(Position position, TileType tileType, TileFeature tileFeature, BorderType[] borders, Resource resource)
	{
		this.position = position;
		this.tileType = tileType;
		this.tileFeature = tileFeature;
		this.borders = borders;
		this.resource = resource;
	}

	public void setTileType(TileType tileType)
	{
		this.tileType = tileType;
	}
	public void setTileFeature(TileFeature tileFeature) {
		this.tileFeature = tileFeature;
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
	public boolean isRuined() {
		return isRuined;
	}

	public void setRuined(boolean ruined) {
		isRuined = ruined;
	}

	public boolean isBlocker()
	{
		return tileType.isBlocker || tileFeature.isBlocker;
	}
	public int distanceTo(Tile tile)
	{
		if(tile == null)
			return -1;
		return Math.abs((Math.abs(this.getPosition().Q - tile.getPosition().Q) + Math.abs(this.getPosition().R - tile.getPosition().R) + Math.abs(this.getPosition().S - tile.getPosition().S)) / 2);
	}
	public Tile clone() //TODO: should be a deep copy
	{
		Tile newTile = new Tile(position.clone(), tileType, tileFeature, null, null);
		BorderType[] newBorders = new BorderType[6];
		for(int i = 0; i < 6; i++)
			newBorders[i] = borders[i];
		newTile.borders = newBorders;
		if(this.resource != null)
			newTile.resource = this.resource.clone();
		newTile.hasRoad = hasRoad;
		newTile.hasRailRoad = hasRailRoad;
		if(combatUnitInTile != null)
		{
			newTile.combatUnitInTile = combatUnitInTile.clone();
			newTile.combatUnitInTile.setTile(newTile);
		}
		if(nonCombatUnitInTile != null)
		{
			newTile.nonCombatUnitInTile = nonCombatUnitInTile.clone();
			newTile.nonCombatUnitInTile.setTile(newTile);
		}
		newTile.isPillaged = isPillaged;
		newTile.isRuined = isRuined;
		return newTile;
	}

	public BorderType getBoarderType(Tile tile) {
		if(this.getPosition().Q - tile.getPosition().Q == 0){
			if(tile.getPosition().R - this.getPosition().R == 1) return this.borders[3];
			else if(tile.getPosition().R - this.getPosition().R == -1) return this.borders[0];
		}
		else if(this.getPosition().R - tile.getPosition().R == 0){
			if(tile.getPosition().Q - this.getPosition().Q == 1) return this.borders[4];
			else if(tile.getPosition().Q - this.getPosition().Q == -1) return this.borders[1];
		}
		else if(this.getPosition().S - tile.getPosition().S == 0){
			if(tile.getPosition().R - this.getPosition().R == 1) return this.borders[2];
			else if(tile.getPosition().R - this.getPosition().R == -1) return this.borders[5];
		}
		return null;
	}
	public Player GetTileRuler(){
		for (Player player : GameController.getInstance().getPlayers()) {
			for (City city : player.getCities()) {
				for (Tile tile : city.getTerritory()) {
					if(tile.getPosition().equals(this.position)) return player;
				}
			}
		}
		return null;
	}
}















