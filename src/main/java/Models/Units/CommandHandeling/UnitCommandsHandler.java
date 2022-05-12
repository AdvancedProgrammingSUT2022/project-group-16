package Models.Units.CommandHandeling;

import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.LongRange;
import Models.Units.NonCombatUnits.*;
import Models.Units.Unit;
import Models.Units.UnitState;

public class UnitCommandsHandler {

    public static void handleCommands(Unit unit, UnitCommands commands){
        if(commands.equals(UnitCommands.GET_READY)) unit.getReady();
        else if(commands.equals(UnitCommands.FORTIFY)) unit.fortify();
        else if(commands.equals(UnitCommands.FORTIFY_TILL_Heel)) unit.fortifyTillHeel();
        else if(commands.equals(UnitCommands.GET_SET)) unit.getSet();
        else if(commands.equals(UnitCommands.AWAKEN)) unit.setUnitState(UnitState.ACTIVE);
        else if(commands.equals(UnitCommands.REMOVE_UNIT)) unit.removeUnit();
        else if(commands.equals(UnitCommands.MOVE)) unit.move(UnitCommands.MOVE.getDestination());
        else if(commands.equals(UnitCommands.ATTACK)) ((CombatUnit) unit).attack();
        else if(commands.equals(UnitCommands.GET_READY_TO_FIGHT)) ((LongRange) unit).getReadyToFight();
        else if(commands.equals(UnitCommands.CREATE_CITY)) ((Settler) unit).createCity();
        else if(commands.equals(UnitCommands.BUILD_ROAD)) ((Worker) unit).buildRoad();
        else if(commands.equals(UnitCommands.BUILD_RAILROAD)) ((Worker) unit).buildRailRoad();
        else if(commands.equals(UnitCommands.BUILD_FARM)) ((Worker) unit).buildFarm();
        else if(commands.equals(UnitCommands.BUILD_MINE)) ((Worker) unit).buildMine();
        else if(commands.equals(UnitCommands.BUILD_TRADING_POST)) ((Worker) unit).buildTradingPost();
        else if(commands.equals(UnitCommands.BUILD_LUMBER_MILL)) ((Worker) unit).buildLumberMill();
        else if(commands.equals(UnitCommands.BUILD_PASTURE)) ((Worker) unit).buildPasture();
        else if(commands.equals(UnitCommands.BUILD_CAMP)) ((Worker) unit).buildCamp();
        else if(commands.equals(UnitCommands.BUILD_PLANTATION)) ((Worker) unit).buildPlantation();
        else if(commands.equals(UnitCommands.BUILD_QUARRY)) ((Worker) unit).buildQuarry();
        else if(commands.equals(UnitCommands.BUILD_FACTORY)) ((Worker) unit).buildFactory();
        else if(commands.equals(UnitCommands.REMOVE_JUNGLE)) ((Worker) unit).removeJungle();
        else if(commands.equals(UnitCommands.REMOVE_FOREST)) ((Worker) unit).removeForest();
        else if(commands.equals(UnitCommands.REMOVE_MARSH)) ((Worker) unit).removeMarsh();
        else if(commands.equals(UnitCommands.REMOVE_ROADS)) ((Worker) unit).removeRoads();
        else if(commands.equals(UnitCommands.REPAIR_TILE)) ((Worker) unit).repairTile();
        else if(commands.equals(UnitCommands.CHANGE_ACTIVATE)) unit.changeActivate();
        else if(commands.equals(UnitCommands.SLEEP)) unit.changeSleepWake();
    }
}
