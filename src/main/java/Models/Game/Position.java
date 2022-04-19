package Models.Game;

public class Position
{
	public final int x;
	public final int y;
	
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
