package Models.Units.NonCombatUnits;

import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.Terrain.TileFeature;
import Models.Terrain.TileType;

import java.util.ArrayList;

public class Worker extends NonCombatUnit{

    private int TurnsTillRepairment = 3;
    private int TurnsTillBuildRoad = 3;
    private int TurnsTillBuildRailRoad = 3;
    private ArrayList<Improvement> improvements = new ArrayList<>();


    public Worker(Player rulerPlayer, Tile tile){
        this.setRulerPlayer(rulerPlayer);
        this.setProductionCost(10);//TODO what is the max health/cost??
        this.setRequiredTechnology(null);
        this.setMovementPoints(1);
        this.setTile(tile);
        tile.setNonCombatUnitInTile(this);
        this.setHealth(100);
        this.setSpeed(1);
        this.setPower(0);
        this.setRequiredResource(null);
        rulerPlayer.addUnit(this);
        addImprovement();
    }

    private void addImprovement(){
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
                this.getTile().getTileType().equals(TileType.TUNDRA) || this.getTile().getTileFeature().equals(TileFeature.ICE) ||
                !this.getRulerPlayer().getTechnologies().contains(Technology.THE_WHEEL))
            return false;
        return true;
    }
    public void buildRailRoad(){
        if(this.getTile().hasRailRoad() == true) return;
       if(this.getTurnsTillBuildRailRoad() == 3 && canBuildRoad())
           this.setTurnsTillBuildRailRoad(2);
       if(this.getTurnsTillBuildRailRoad() == 0) {
           this.getTile().setHasRailRoad(true);
       }
    }
    public void buildRoad(){
        if(this.getTile().hasRoad() == true) return;
        if(this.getTurnsTillBuildRoad() == 3 && canBuildRoad())
            this.setTurnsTillBuildRoad(2);
        if(this.getTurnsTillBuildRoad() == 0) {
            this.getTile().setHasRoad(true);
        }
    }
    public void buildFarm(){
        if(this.getTile().getImprovement() != null || !this.getRulerPlayer().getTechnologies().contains(Technology.AGRICULTURE)) return;
        if(this.getTile().getImprovement() == null && this.improvements.get(1).inLineTurn == 0){
            this.getTile().setImprovement(Improvement.FARM);
            removeForest();
            removeJungle();
            removeMarsh();
            return;
        }

        if(this.getTile().getTileType().equals(TileType.TUNDRA) || this.getTile().getTileFeature().equals(TileFeature.ICE) ||
        this.getTile().getResource() != null) return;

        if(this.getTile().getTileFeature().equals(TileFeature.FOREST) && this.getRulerPlayer().getTechnologies().contains(Technology.MINING)){
            this.improvements.get(0).turnToConstruct = 10;
            this.improvements.get(0).inLineTurn -- ;
        }
        else if(this.getTile().getTileFeature().equals(TileFeature.JUNGLE) && this.getRulerPlayer().getTechnologies().contains(Technology.BRONZE_WORKING)){
            this.improvements.get(0).turnToConstruct = 13;
            this.improvements.get(0).inLineTurn -- ;
        }
        else if(this.getTile().getTileFeature().equals(TileFeature.MARSH) && this.getRulerPlayer().getTechnologies().contains(Technology.MASONRY)){
            this.improvements.get(0).turnToConstruct = 12;
            this.improvements.get(0).inLineTurn -- ;
        }
        else if(this.getTile().getTileFeature().equals(TileFeature.FLOOD_PLAIN) && this.getTile().getTileType().equals(TileType.DESERT)
                && this.getTile().getTileType().equals(TileType.GRASSLAND)){
            this.improvements.get(0).turnToConstruct = 6;
            this.improvements.get(0).inLineTurn -- ;
        }

    }
    public void buildMine(){
        if(this.getTile().getImprovement() != null) return;
        if(this.getTile().getImprovement() == null && this.improvements.get(3).inLineTurn == 0){
            this.getTile().setImprovement(Improvement.MINE);
            removeForest();
            removeJungle();
            removeMarsh();
            return;
        }

        if(!this.getTile().getTileType().equals(TileType.HILLS) || this.getTile().getResource() == null ||
                !this.getRulerPlayer().getTechnologies().contains(Technology.MINING)) return;

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

    }
    public void buildTradingPost(){
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.TRAPPING) &&
                (this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                        this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA)))
            this.getTile().setImprovement(Improvement.TRADING_POST);

    }
    public void buildLumberMill(){
        if(this.getTile().getImprovement() == null && this.getTile().getTileFeature().equals(TileFeature.JUNGLE) &&
        this.getRulerPlayer().getTechnologies().contains(Technology.CONSTRUCTION))
            this.getTile().setImprovement(Improvement.LUMBER_MILL);
    }
    public void buildPasture(){
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.ANIMAL_HUSBANDRY) &&
                (this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                        this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA)) ||
                this.getTile().getTileType().equals(TileType.HILLS))
            this.getTile().setImprovement(Improvement.PASTURE);
    }
    public void buildCamp(){
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.TRAPPING) &&
                (this.getTile().getTileFeature().equals(TileFeature.JUNGLE) || this.getTile().getTileType().equals(TileType.PLAINS) ||
                        this.getTile().getTileType().equals(TileType.TUNDRA)) || this.getTile().getTileType().equals(TileType.HILLS))
            this.getTile().setImprovement(Improvement.CAMP);
    }
    public void buildPlantation(){
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.CALENDAR) &&
                (this.getTile().getTileFeature().equals(TileFeature.JUNGLE) || this.getTile().getTileType().equals(TileType.PLAINS) ||
                        this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                        this.getTile().getTileFeature().equals(TileFeature.FOREST) || this.getTile().getTileFeature().equals(TileFeature.MARSH) ||
                this.getTile().getTileFeature().equals(TileFeature.FLOOD_PLAIN)))
            this.getTile().setImprovement(Improvement.PLANTATION);
    }
    public void buildQuarry(){
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.MASONRY) &&
                (this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA) ||
                        this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                        this.getTile().getTileType().equals(TileType.HILLS)))
            this.getTile().setImprovement(Improvement.QUARRY);
    }
    public void buildFactory(){
        if(this.getTile().getImprovement() == null && this.getRulerPlayer().getTechnologies().contains(Technology.ENGINEERING) &&
                (this.getTile().getTileType().equals(TileType.PLAINS) || this.getTile().getTileType().equals(TileType.TUNDRA) ||
                        this.getTile().getTileType().equals(TileType.DESERT) || this.getTile().getTileType().equals(TileType.GRASSLAND) ||
                        this.getTile().getTileType().equals(TileType.SNOW)))
            this.getTile().setImprovement(Improvement.FACTORY);
    }
    public void removeJungle(){
        if(this.getTile().getTileFeature().equals(TileFeature.JUNGLE))
            this.getTile().setTileFeature(null);
    }
    public void removeForest(){
        if(this.getTile().getTileFeature().equals(TileFeature.FOREST))
            this.getTile().setTileFeature(null);
    }
    public void removeMarsh(){
        if(this.getTile().getTileFeature().equals(TileFeature.MARSH))
            this.getTile().setTileFeature(null);
    }
    public void removeRoads(){
        this.getTile().setHasRailRoad(false);
        this.getTile().setHasRoad(false);
    }
    public void repairTile(){
        if(!this.getTile().isRuined()) return;
        if(getTurnsTillRepairment() == 0 && this.getTile().isRuined()){
            this.getTile().setRuined(false);
            this.setTurnsTillRepairment(3);
            return;
        }
        setTurnsTillRepairment(getTurnsTillRepairment() - 1);
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
