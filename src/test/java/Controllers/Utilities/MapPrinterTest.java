package Controllers.Utilities;

import Controllers.GameController;
import Models.Player.Civilization;
import Models.Player.Player;
import org.junit.jupiter.api.Test;

class MapPrinterTest
{
	GameController gameController;
	Player player;
	
	public MapPrinterTest()
	{
		gameController = GameController.getInstance();
		player = new Player(Civilization.PERSIAN, "DashReza7", "Reza", "Password1@");
	}
	
	@Test
	void getMapString()
	{
		System.out.println(MapPrinter.getMapString(player.getMap(), gameController.MAX_MAP_SIZE, gameController.MAX_MAP_SIZE));
	}
}














