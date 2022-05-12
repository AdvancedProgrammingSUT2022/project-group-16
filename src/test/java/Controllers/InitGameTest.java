package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.City.City;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Terrain.Improvement;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.LongRangeType;
import Models.Units.Unit;
import Models.Units.UnitState;
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
		gameController.addPlayer(new Player(Civilization.AMERICAN, "Player 1", "p1", "123", 0));
		gameController.addPlayer(new Player(Civilization.ARABIAN, "Player 2", "p2", "123", 0));
	}
	
	@Test
	void testInitGame()
	{
		gameController.initGame();
		playerTurn = gameController.getPlayerTurn();
		
		playerTurn.getUnits().get(0).setUnitState(UnitState.ACTIVE);
		City city = new City(playerTurn.getTileByXY(5, 5), playerTurn);
		playerTurn.getTileByXY(5, 5).setImprovement(Improvement.PLANTATION);
		
		System.out.println(gameController.getMapString());
	}
}



















