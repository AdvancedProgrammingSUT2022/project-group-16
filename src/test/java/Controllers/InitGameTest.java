package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Player.Civilization;
import Models.Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InitGameTest
{
	GameController gameController;
	Player playerTurn;
	
	@BeforeEach
	void setUp()
	{
		gameController = GameController.getInstance();
		gameController.addPlayer(new Player(Civilization.PERSIAN, "Player 1", "p1", "123"));
		gameController.addPlayer(new Player(Civilization.ARABIAN, "Player 2", "p2", "123"));
	}
	
	@Test
	void testInitGame()
	{
		gameController.initGame();
		playerTurn = gameController.getPlayerTurn();
		
		// initial map
		System.out.println(MapPrinter.getMapString(playerTurn));
		// SOME CHANGES TO THE MAP
		playerTurn.updateTileStates();
		System.out.println(MapPrinter.getMapString(playerTurn));
	}
}



















