import com.diogonunes.jcolor.*;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println(Ansi.colorize(String.format("%10s", "Tilesasldjasldjasldksa:"), Attribute.GREEN_BACK(), Attribute.WHITE_TEXT(), Attribute.BOLD()));
	}
}
