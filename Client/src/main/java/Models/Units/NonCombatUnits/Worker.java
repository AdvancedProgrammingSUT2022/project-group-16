package Models.Units.NonCombatUnits;

import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.Terrain.TileFeature;
import Models.Terrain.TileType;

import java.util.ArrayList;

public class Worker extends NonCombatUnit {

    private int TurnsTillRepairment = 3;
    private int TurnsTillBuildRoad = 3;
    private int TurnsTillBuildRailRoad = 3;
    private ArrayList<Improvement> improvements = new ArrayList<>();


    public Worker(Player rulerPlayer, Tile tile) {
        this.setRulerPlayer(rulerPlayer);
        this.setProductionCost(10);//TODO what is the max health/cost??
        this.setRequiredTechnology(null);
        this.setMovementPoints(1);
        this.setMP(1);
        this.setTile(tile);
        tile.setNonCombatUnitInTile(this);
        this.setSpeed(1);
        this.setPower(0);
        this.setRequiredResource(null);
        rulerPlayer.addUnit(this);
        addImprovement();
    }

    //for mocking a unit while constructing in city
    public Worker() {
        this.setProductionCost(10);
        this.setRequiredTechnology(null);
        this.setMovementPoints(1);
        this.setMP(1);
        this.setSpeed(1);
        this.setPower(0);
        this.setRequiredResource(null);
        addImprovement();
    }

    private void addImprovement() {
        improvements.add(Improvement.FARM);
        improvements.add(Improvement.MINE);
        /*if the following had turns till construction , don't comment them
        improvements.add(Improvement.CAMP);
        improvements.add(Improvement.LUMBER_MILL);
        improvements.add(Improvement.PASTURE);
        improvements.add(Improvement.PLANTATION);
        improvements.add(Improvement.QUARRY);
        improvements.add(Improvement.TRADING_POST);
        improvements.add(Improvement.FACTORY);*/
    }

    public ArrayList<Improvement> getImprovements() {
        return improvements;
    }
    public int getTurnsTillRepairment() {
        return TurnsTillRepairment;
    }
    public void setTurnsTillRepairment(int turnsTillRepairment) {
        TurnsTillRepairment = turnsTillRepairment;
    }
    public int getTurnsTillBuildRoad() {
        return TurnsTillBuildRoad;
    }
    public void setTurnsTillBuildRoad(int turnsTillBuildRoad) {
        TurnsTillBuildRoad = turnsTillBuildRoad;
    }
    public int getTurnsTillBuildRailRoad() {
        return TurnsTillBuildRailRoad;
    }
    public void setTurnsTillBuildRailRoad(int turnsTillBuildRailRoad) {
        TurnsTillBuildRailRoad = turnsTillBuildRailRoad;
    }



    private boolean canBuildRoad(){
        if(this.getTile().getTileType().equals(TileType.OCEAN) || this.getTile().getTileType().equals(TileType.MOUNTAIN) ||
                this.getTile().getTileType().equals(TileType.TUNDRA) || this.getTile().getTileFeature().equals(TileFeature.ICE))
            return false;
        return true;
    }
    public String buildRailRoad(){
        if(this.getTurnsTillBuildRailRoad() == 0) {
            this.getTile().setHasRailRoad(true);
            this.setTurnsTillBuildRailRoad(3);
            return null;
        }
        if(this.getTurnsTillBuildRailRoad() < 3){
            this.setTurnsTillBuildRailRoad(this.getTurnsTillBuildRailRoad() - 1);
            return null;
        }
       if(!canBuildRoad())
           return "cannot build railRoad in this Tile";
       this.setTurnsTillBuildRailRoad(2);
       return null;
    }
    public String buildRoad(){
        if(this.getTurnsTillBuildRoad() == 0) {
            this.getTile().setHasRoad(true);
            this.setTurnsTillBuildRoad(3);
            return null;
        }
        if(this.getTurnsTillBuildRoad() < 3){
            this.setTurnsTillBuildRoad(this.getTurnsTillBuildRoad() - 1);
            return null;
        }
        if(this.getTile().hasRoad() == true) return "the tile has road";
        if(!canBuildRoad()) return "cannot build road here";
        this.setTurnsTillBuildRoad(2);
        return null;
    }
    public String buildFarm(){
        if(this.getTile().getImprovement() == Improvement.NONE && this.improvements.get(1).inLineTurn == 0)
        {
            this.getTile().setImprovement(Improvement.FARM);
            removeForest();
            removeJungle();
            removeMarsh();
        }
        if(this.improvements.get(0).inLineTurn < this.improvements.get(0).turnToConstruct ){
            this.improvements.get(0).inLineTurn --;
            return null;
        }

        if(this.getTile().getTileType().equals(TileType.TUNDRA) || (this.getTile().getTileFeature() != null && this.getTile().getTileFeature().equals(TileFeature.ICE)) ||
            this.getTile().getResource() != null)
            return "can't build farm on this tile";

        if(this.getTile().getTileFeature() != null && this.getTile().getTileFeature().equals(TileFeature.FOREST) && this.getRulerPlayer().getTechnologies().contains(Technology.MINING)){
            this.improvements.get(0).turnToConstruct = 10;
            this.improvements.get(0).inLineTurn--;
            return null;
        }
        else if(this.getTile().getTileFeature() != null && this.getTile().getTileFeature().equals(TileFeature.JUNGLE) && this.getRulerPlayer().getTechnologies().contains(Technology.BRONZE_WORKING)){
            this.improvements.get(0).turnToConstruct = 13;
            this.improvements.get(0).inLineTurn -- ;
            return null;
        }
        else if(this.getTile().getTileFeature() != null && this.getTile().getTileFeature().equals(TileFeature.MARSH) && this.getRulerPlayer().getTechnologies().contains(Technology.MASONRY)){
            this.improvements.get(0).turnToConstruct = 12;
            this.improvements.get(0).inLineTurn -- ;
            return null;
        }
        else if(this.getTile().getTileFeature() != null && this.getTile().getTileFeature().equals(TileFeature.FLOOD_PLAIN) || (this.getTile().getTileType().equals(TileType.DESERT)
                || this.getTile().getTileType().equals(TileType.GRASSLAND))){
            this.improvements.get(0).turnToConstruct = 6;
            this.improvements.get(0).inLineTurn -- ;
            return null;
        }
        return "can not build  farm in this tile";
    }
    public String buildMine(){
        if(this.getTile().getImprovement() == null && this.improvements.get(3).inLineTurn == 0){
            this.getTile().setImprovement(Improvement.MINE);
            removeForest();
            removeJungle();
            removeMarsh();
            this.getCommands().remove(0);
            return null;
        }

        if(!this.getTile().getTileType().equals(TileType.HILLS) || this.getTile().getResource() == null ||
                !this.getRulerPlayer().getTechnologies().contains(Technology.MINING)) return "can't build mine in this tile";

        if(this.getTile().getTileFeature().equals(TileFeature.FOREST)){
            this.improvements.get(1).turnToConstruct = 10;
            this.improvements.get(1).inLineTurn -- ;
        }
        else if(this.getTile().getTileFeature().equals(TileFeature.JUNGLE) && this.getRulerPlayer().getTechnologies().contains(Technology.BRONZE_WORKING)){
            this.improvements.get(1).turnToConstruct = 13;
            this.improvements.get(1).inLineTurn -- ;
        }
        else if(this.getTile().getTileFeature().equals(TileFeature.MARSH) && this.getRulerPlayer().getTechnologies().contains(Technology.MASONRY)){
            this.improvements.get(1).turnToConstruct = 12;
            this.improvements.get(1).inLineTurn -- ;
        }
        else if(!this.getTile().getTileFeature().equals(TileFeature.JUNGLE) && !this.getTile().getTileFeature().equals(TileFeature.MARSH)
        && !this.getTile().getTileFeature().equals(TileFeature.FOREST)){
            this.improvements.get(1).turnToConstruct = 6;
            this.improvements.get(1).inLineTurn -- ;
        }
        return null;
    }
    public String buildTradingPost(){
        if((this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA)))
        {
            this.getTile().setImprovement(Improvement.TRADING_POST);
            return null;
        }
        return "can't build trading post in this tile";

    }
    public String buildLumberMill(){
        if(this.getTile().getTileFeature().equals(TileFeature.JUNGLE)){
            this.getTile().setImprovement(Improvement.LUMBER_MILL);
            return null;
        }
        return "can't build lumberMill in this tile";
    }
    public String buildPasture(){
        if((this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA)) ||
                this.getTile().getTileType().equals(TileType.HILLS))
        {
            this.getTile().setImprovement(Improvement.PASTURE);
            return null;
        }
        return "can't build pasture in this tile";

    }
    public String buildCamp(){
        if((this.getTile().getTileFeature().equals(TileFeature.JUNGLE) || this.getTile().getTileType().equals(TileType.PLAINS) ||
                        this.getTile().getTileType().equals(TileType.TUNDRA)) || this.getTile().getTileType().equals(TileType.HILLS))
        {
            this.getTile().setImprovement(Improvement.CAMP);
            return null;
        }
        return "can't build camp in this tile";
    }
    public String buildPlantation()
    {
        if((this.getTile().getTileFeature().equals(TileFeature.JUNGLE) || this.getTile().getTileType().equals(TileType.PLAINS) ||
                this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                this.getTile().getTileFeature().equals(TileFeature.FOREST) || this.getTile().getTileFeature().equals(TileFeature.MARSH) ||
                this.getTile().getTileFeature().equals(TileFeature.FLOOD_PLAIN)))
        {
            this.getTile().setImprovement(Improvement.PLANTATION);
            return null;
        }
        return "can't build plantation in this tile";
    }
    public String buildQuarry()
    {
        if((this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA) ||
                this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                this.getTile().getTileType().equals(TileType.HILLS)))
        {
            this.getTile().setImprovement(Improvement.QUARRY);
            return null;
        }
        return "can't build quarry in this tile";
    }
    public String buildFactory()
    {
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.ENGINEERING) &&
                (this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA) ||
                        this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                        this.getTile().getTileType().equals(TileType.SNOW))) {
            this.getTile().setImprovement(Improvement.FACTORY);
            return null;
        }
        return "can't build factory in this tile";
    }
    public String removeJungle(){
        if(this.getTile().getTileFeature().equals(TileFeature.JUNGLE)) {
            this.getTile().setTileFeature(null);
            return null;
        }
        return "the tile doesn't have Jungle";
    }
    public String removeForest(){
        if(this.getTile().getTileFeature().equals(TileFeature.FOREST)) {
            this.getTile().setTileFeature(null);
            return null;
        }
        return "the tile doesn't have Forest";
    }
    public String removeMarsh(){
        if(this.getTile().getTileFeature().equals(TileFeature.MARSH)) {
            this.getTile().setTileFeature(null);
            return null;
        }
        return "tile doesn't have Marsh";
    }
    public String removeRoads(){
        if(this.getTile().hasRailRoad() || this.getTile().hasRoad()) {
            this.getTile().setHasRailRoad(false);
            this.getTile().setHasRoad(false);
            return null;
        }
        return "the tile doesn't have roads";
    }
    public String repairTile(){
        if(!this.getTile().isRuined()) return "there is nothing to repair";
        if(getTurnsTillRepairment() == 0 && this.getTile().isRuined()){
            this.getTile().setRuined(false);
            this.setTurnsTillRepairment(3);
            return null;
        }
        setTurnsTillRepairment(getTurnsTillRepairment() - 1);
        return null;
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
