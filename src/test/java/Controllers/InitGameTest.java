package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.City.City;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Terrain.Improvement;
import Models.Terrain.Position;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.LongRangeType;
import Models.Units.Unit;
import Models.Units.UnitState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InitGameTest
{
	GameController gameController;
	
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
		
		System.out.println("food: " + gameController.getPlayerTurn().getFood() + " gold: " + gameController.getPlayerTurn().getGold());
		
		// make a city in the middle of the map
		new City(gameController.getPlayerTurn().getTileByXY(5, 5), gameController.getPlayerTurn());
		new City(gameController.getPlayerTurn().getTileByXY(2, 5), gameController.getPlayers().get(1));
		
		

		System.out.println(gameController.getMapString());
	}
}



















