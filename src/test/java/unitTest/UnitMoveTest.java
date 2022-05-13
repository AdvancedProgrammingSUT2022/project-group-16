package unitTest;
import Models.Game.Position;
import Models.Player.Player;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.LongRange;
import Models.Units.FindWay;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.NonCombatUnits.Worker;
import Models.Units.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class UnitMoveTest {
    @Mock
    Player player;
    @Mock
    Tile tile;
    @Mock
    Settler l;

    @Test
    public void MoveTest(){
        Worker worker = new Worker(player,tile);
        Tile t = new Tile(new Position(2,4), null,null,null,null);
        String a = worker.move(null);
        Assertions.assertArrayEquals("no destination".toCharArray(), a.toCharArray());

//        ArrayList<Position> moves = new ArrayList<>();
//        moves.add(new Position(2,3));
//        moves.add(new Position(3,4));
//        when(FindWay.getInstance().getMoves()).thenReturn(moves);
//        when(worker.getMovementPoints()).thenReturn(0);
//        String b = worker.move(t);
//        Assertions.assertArrayEquals("no movementPoints".toCharArray(),b.toCharArray());
    }
    @Test
    public void UpdateUnitMovementsTest(){
        Player player = new Player(null,"d","s","1",0);
        Worker worker = new Worker(player,tile);
        worker.setMovementPoints(0);
        String a = worker.updateUnitMovements();
        Assertions.assertArrayEquals("no movementPoints".toCharArray(), a.toCharArray());
    }
    @Test
    public void isThereAnotherUnitInTileTest(){
        Tile t = new Tile(new Position(2,4), null,null,null,null);
        Player player = new Player(null,"d","s","1",0);
        Worker worker = new Worker(player,t);
        when(tile.getNonCombatUnitInTile()).thenReturn(l);
        Assertions.assertTrue(worker.isThereAnotherUnitInTile(tile));
    }

}
