package Models.Game;

import Controllers.GameController;
import Models.Terrain.Tile;

import java.util.Objects;

public class Position
{
	/*
	 *   --> y
	 *   |
	 *   |
	 *   x
	 *
	 *   row == x
	 *   col == y
	 * */
	// oddQ_offset_coordinate
	public final int X;
	public final int Y;
	// cube_coordinate
	public final int Q;
	public final int R;
	public final int S;
	
	public Position(int x, int y)
	{
		this.X = x;
		this.Y = y;
		this.Q = y;
		this.R = x - (y - (y & 1)) / 2;
		this.S = this.Q * (-1) + this.R * (-1);
	}
	public Position(int q, int r, int s)
	{
		this.Q = q;
		this.R = r;
		this.S = s;
		assert q + r + s == 0;
		this.Y = q;
		this.X = r + (q - (q & 1)) / 2;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Position position = (Position) o;
		return X == position.X && Y == position.Y && Q == position.Q && R == position.R && S == position.S;
	}
	@Override
	public int hashCode()
	{
		return Objects.hash(X, Y, Q, R, S);
	}
	public Position clone()
	{
		return new Position(X, Y);
	}

}

















