package Models.Units.NonCombatUnits;

import Models.Player.Player;
import Models.Terrain.Tile;

public class Worker extends NonCombatUnit{

    private int TurnsTillRepairment = 3;

    public Worker(Player rulerPlayer, int cost, int movement, Tile tile, int speed, int power){
        this.setRulerPlayer(rulerPlayer);
        this.setProductionCost(cost);
        this.setRequiredTechnology(null);
        this.setMovementPoints(movement);
        this.setTile(tile);
        this.setHealth(100); //TODO what is the max health,speed,power?
        this.setSpeed(speed);
        this.setPower(power);
        this.setRequiredResource(null);
        rulerPlayer.addUnit(this);
    }

    public int getTurnsTillRepairment() {
        return TurnsTillRepairment;
    }

    public void setTurnsTillRepairment(int turnsTillRepairment) {
        TurnsTillRepairment = turnsTillRepairment;
    }

    public void buildDevelopment(String name){

    }
    public void repairDevelopment(){

    }
    public void removeDevelopment(){

    }
    public void buildRailRoad(){
        //TODO check if it is possible to pass the tile
        this.getTile().setHasRailRoad(true);
    }
    public void buildRoad(){
        //TODO check if it is possible to pass the tile
        this.getTile().setHasRoad(true);
    }
    public void buildFarm(){

    }
    public void buildMine(){

    }
    public void buildTradingPost(){

    }
    public void buildSawMill(){

    }
    public void buildPasture(){

    }
    public void buildCamp(){

    }
    public void buildPlantation(){

    }
    public void buildQuarry(){

    }
    public void removeJungle(){

    }
    public void removeForest(){

    }
    public void removeMarsh(){

    }
    public void removeRoads(){
        this.getTile().setHasRailRoad(false);
        this.getTile().setHasRoad(false);
    }
    public void repairTile(){

    }
    
    @Override
    public String toString()
    {
        return "WORKER";
    }
}
