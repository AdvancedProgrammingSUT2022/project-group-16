package Controllers;

import Models.City.City;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.*;
import Views.gameMenuView;
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
		
		gameController.getTileByXY(5, 5).setImprovement(Improvement.PLANTATION);
		
		System.out.println(gameController.getMapString());
	}
}



















