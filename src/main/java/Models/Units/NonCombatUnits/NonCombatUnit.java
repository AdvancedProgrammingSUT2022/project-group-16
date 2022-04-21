package Models.Units.NonCombatUnits;

import Models.Terrain.Tile;
import Models.Units.Unit;

public abstract class NonCombatUnit extends Unit
{
    private Tile workingTile;
    protected abstract void move();
}
