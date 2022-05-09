package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Player.Civilization;
import Models.Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InitGameTest
{
	GameController gameController;
	
	@BeforeEach
	void setUp()
	{
		gameController = GameController.getInstance();
		gameController.addPlayer(new Player(Civilization.PERSIAN, "Player 1", "p1", "123"));
		gameController.addPlayer(new Player(Civilization.ARABIAN, "Player 2", "p2", "123"));
		gameController.addPlayer(new Player(Civilization.MAYAN, "Player 3", "p3", "123"));
		gameController.addPlayer(new Player(Civilization.GREEK, "Player 4", "p4", "123"));
	}
	
	@Test
	void testInitGame()
	{
		gameController.initGame();
		
		gameController.getPlayerTurn().setSelectedUnit(gameController.getPlayerTurn().getUnits().get(1));
		
		System.out.println(MapPrinter.getMapString(gameController.getPlayerTurn()));
		gameController.changeTurn();
		System.out.println(MapPrinter.getMapString(gameController.getPlayerTurn()));
		
	}
}



















