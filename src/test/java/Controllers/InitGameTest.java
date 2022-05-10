package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.LongRangeType;
import Models.Units.Unit;
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
		gameController.addPlayer(new Player(Civilization.PERSIAN, "Player 1", "p1", "123", 0));
		gameController.addPlayer(new Player(Civilization.ARABIAN, "Player 2", "p2", "123", 0));
	}
	
	@Test
	void testInitGame()
	{
		gameController.initGame();
		playerTurn = gameController.getPlayerTurn();
		
		// initial map
		System.out.println(gameController.getMapString());
		new LongRange(playerTurn, LongRangeType.ARTILLERY, playerTurn.getTileByXY(5, 4));
			
	}
}



















