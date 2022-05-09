package Models.Units.NonCombatUnits;

import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.Tile;
import Models.Terrain.TileFeature;
import Models.Terrain.TileType;

public class Worker extends NonCombatUnit{

    private int TurnsTillRepairment = 3;

    public Worker(Player rulerPlayer, Tile tile){
        this.setRulerPlayer(rulerPlayer);
        this.setProductionCost(10);//TODO what is the max health/cost??
        this.setRequiredTechnology(null);
        this.setMovementPoints(1);
        this.setTile(tile);
        this.setHealth(100);
        this.setSpeed(1);
        this.setPower(0);
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
    private boolean canBuildRoad(){
        if(this.getTile().getTileType().equals(TileType.OCEAN) || this.getTile().getTileType().equals(TileType.MOUNTAIN) ||
                this.getTile().getTileType().equals(TileType.TUNDRA) || this.getTile().getTileFeature().equals(TileFeature.ICE) ||
                !this.getRulerPlayer().getTechnologies().contains(Technology.THE_WHEEL))
            return false;
        return true;
    }
    public void buildRailRoad(){
       if(canBuildRoad())
           this.getTile().setHasRailRoad(true);
    }
    public void buildRoad(){
        if(canBuildRoad())
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
    public Worker clone(){
        Worker newWorker = new Worker(this.getRulerPlayer(), this.getTile());
        newWorker.setHealth(this.getHealth());
        return newWorker;
    }
}
