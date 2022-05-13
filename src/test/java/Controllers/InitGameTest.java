package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.City.City;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Player.TileState;
import Models.Terrain.*;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.LongRangeType;
import Models.Units.Unit;
import Models.Units.UnitState;
import Views.gameMenuView;
import org.junit.jupiter.api.Assertions;
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
		
		for(Tile tile : gameController.getMap())
			tile.setTileType(TileType.MOUNTAIN);
		gameController.getPlayerTurn().updateTileStates();
		
		gameMenuView.showBaseFields();
	}
}



















