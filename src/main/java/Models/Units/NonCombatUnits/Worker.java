package Models.Units.NonCombatUnits;

public class Worker extends NonCombatUnit{

    private int TurnsTillRepairment = 3;


    public Worker(){

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
