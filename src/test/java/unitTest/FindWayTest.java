package unitTest;

import Models.Terrain.Position;
import Models.Units.FindWay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FindWayTest {
    @Test
    public void calculateDistanceTest(){
        FindWay findWay = FindWay.getInstance();
        Position a = new Position(2,3);
        Position b = new Position(6,7);
        double result = findWay.calculateDistance(a,b);
        Assertions.assertEquals(5.656854249492381, result);
    }
    @Test
    public void checkFindWayGetDirection(){
        FindWay findWay = FindWay.getInstance();
        Position a = new Position(0,0);
        Position b = new Position(0,3);
        Position ans = findWay.getDirection(a,b,a.Y);
        Assertions.assertEquals(0,ans.X);
        Assertions.assertEquals(1,ans.Y);
    }
    @Test
    public void calculateShortestWayTest(){
        FindWay findWay = FindWay.getInstance();
        Position a = new Position(0,0);
        Position b = new Position(2,3);
        findWay.calculateShortestWay(a,b);
        int[] x = new int[4];
        int[] y = new int[4];
        int i = 0;
        for (Position move : findWay.getMoves()) {
            x[i] = move.X;
            y[i] = move.Y;
            i++;
        }
        int[] ansx = {0,1,2,2};
        int[] ansy = {1,2,2,3};
        Assertions.assertArrayEquals(ansx, x);
        Assertions.assertArrayEquals(ansy, y);
    }
}
