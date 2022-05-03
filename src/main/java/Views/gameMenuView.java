package Views;

import Controllers.GameController;
import Controllers.Utilities.MapPrinter;
import Models.City.City;
import Models.Game.Position;
import Models.Player.Player;
import Models.Terrain.Tile;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.Unit;
import Models.User;
import enums.cheatCode;
import enums.gameCommands.infoCommands;
import enums.gameCommands.mapCommands;
import enums.gameCommands.selectCommands;
import enums.gameCommands.unitCommands;
import enums.gameEnum;
import enums.mainCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;

public class gameMenuView
{
    public static City selectedCity = null;
    public static Unit selectedUnit = null;

    private static void showCity(int number)
    {
        System.out.println(GameController.getPlayerTurn().getCities().get(number).getFoodYield());
        System.out.println(GameController.getPlayerTurn().getCities().get(number).getProductionYield());
        System.out.println(GameController.getPlayerTurn().getCities().get(number).getGoldYield());
        //TODO: print sience
        //TODO: turns til repopulation
    }
    private static void pickCivilizations(Scanner scanner, User[] tmpUsers, ArrayList<Player> players, GameController gameController)
    {
        for(int i = 0; i < tmpUsers.length; i++)
        {
            System.out.println(tmpUsers[i].getUsername() + gameEnum.pickCivilization.regex);
            System.out.println(gameEnum.AMERICAN.regex);
            System.out.println(gameEnum.ARABIAN.regex);
            System.out.println(gameEnum.ASSYRIAN.regex);
            System.out.println(gameEnum.CHINESE.regex);
            System.out.println(gameEnum.GERMAN.regex);
            System.out.println(gameEnum.GREEK.regex);
            System.out.println(gameEnum.MAYAN.regex);
            System.out.println(gameEnum.PERSIAN.regex);
            System.out.println(gameEnum.OTTOMAN.regex);
            System.out.println(gameEnum.RUSSIAN.regex);
            int number;
            do
            {
                number = 0;
                while (number == 0)
                {
                    number = GameController.getNum(scanner, 1, 10);
                    if(number == 0)
                        System.out.println(mainCommands.pickBetween.regex + "1 and 10");
                }
                System.out.println(GameController.pickCivilization(players, tmpUsers, scanner, gameController, number, i));
            } while (number > 10 || number < 1 || GameController.inArr(players, GameController.findCivilByNumber(number)));

            GameController.addPlayer(new Player(GameController.findCivilByNumber(number), tmpUsers[i].getUsername(),
                    tmpUsers[i].getNickname(), tmpUsers[i].getPassword()));
        }
    }

    public static void startGame(Scanner scanner, HashMap<String, String> Map)
    {
        GameController gameController = GameController.getInstance();
        Matcher matcher;
        User[] tmpUsers = GameController.convertMapToArr(Map); //Note: player[0] is loggedInUSer! [loggedInUser, player1, player2, ...]
        ArrayList<Player> players = new ArrayList<>();
        pickCivilizations(scanner, tmpUsers, players, gameController);

        GameController.setFirstPlayer();
        while (true)
        {
            System.out.println(GameController.getPlayerTurn().getUsername() + gameEnum.turn.regex);

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();

                /*cheat codes*/
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null) //TODO
                    System.out.println(GameController.increaseGold(matcher, GameController.getPlayerTurn()));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //TODO
                    System.out.println(GameController.increaseTurns(matcher, GameController.getPlayerTurn()));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null) //done
                    System.out.println(GameController.increaseFood(matcher, GameController.getPlayerTurn()));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null) //done
                    System.out.println(GameController.addTechnology(matcher, GameController.getPlayerTurn()));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.winBattle)) != null) //TODO
                    System.out.println(GameController.winBattle(matcher, GameController.getPlayerTurn()));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null) //TODO
                    System.out.println(GameController.moveUnit(matcher, GameController.getPlayerTurn()));

                /*Info*/ //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoResearch)) != null)
                    System.out.println(gameController.showResearch());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoUnits)) != null)
                    System.out.println(gameController.showUnits());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoCities)) != null)
                    System.out.println(gameController.showCities());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDiplomacy)) != null)
                    System.out.println(gameController.showDiplomacy());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoVictory)) != null)
                    System.out.println(gameController.showVictory());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDemographics)) != null)
                    System.out.println(gameController.showDemographics());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoNotifications)) != null)
                    System.out.println(gameController.showNotifications());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoMilitary)) != null)
                    System.out.println(gameController.showMilitary());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoEconomic)) != null)
                    System.out.println(gameController.showEconomics());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDiplomatic)) != null)
                    System.out.println(gameController.showDiplomatic());
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDeals)) != null)
                    System.out.println(gameController.showDeals());
                    
                /*Select*/
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectCombat)) != null)
                {
                    String tmp = GameController.selectCUnit(command);
                    if(GameController.isValid(tmp))
                    {
                        if(Integer.parseInt(tmp) == -1)
                            System.out.println(mainCommands.invalidCommand);
                        else
                        {
                            //TODO: show combat unit information
                            selectedUnit = GameController.getPlayerTurn().getUnits().get(Integer.parseInt(tmp));
                        }
                    }
                    else
                        System.out.println(tmp);
                }
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectNonCombat)) != null)
                {
                    String tmp = GameController.selectNUnit(command);
                    if(GameController.isValid(tmp))
                    {
                        if(Integer.parseInt(tmp) == -1)
                            System.out.println(mainCommands.invalidCommand);
                        else
                        {
                            //TODO: show nonCombat unit information
                            selectedUnit = GameController.getPlayerTurn().getUnits().get(Integer.parseInt(tmp));
                        }
                    }
                    else
                        System.out.println(tmp);
                }
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectCity)) != null)
                {
                    String tmp = GameController.selectCity(command);
                    if(GameController.isValid(tmp))
                    {
                        if(Integer.parseInt(tmp) == -1)
                            System.out.println(mainCommands.invalidCommand);
                        else
                        {
                            showCity(Integer.parseInt(tmp));
                            selectedCity = GameController.getPlayerTurn().getCities().get(Integer.parseInt(tmp));
                        }
                    }
                    else
                        System.out.println(tmp);
                }

                /*unit*/
                else if((matcher = unitCommands.compareRegex(command, unitCommands.moveTo)) != null)
                    GameController.moveUnit(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.sleep)) != null)
                    GameController.sleep(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.alert)) != null)
                    GameController.alert(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.fortify)) != null)
                    GameController.fortify(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.fortifyHeal)) != null)
                    GameController.fortifyTilHeal(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.garrison)) != null)
                    GameController.garrison(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.setup)) != null)
                    GameController.setup(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.attack)) != null)
                    GameController.attack(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.foundCity)) != null)
                    GameController.found(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.cancelMission)) != null)
                    GameController.cancel(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.wake)) != null)
                    GameController.wake(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.delete)) != null)
                    GameController.delete(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildRoad)) != null)
                    GameController.road(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildRailRoad)) != null)
                    GameController.railRoad(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildFarm)) != null)
                    GameController.farm(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildMine)) != null)
                    GameController.mine(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildTradingPost)) != null)
                    GameController.tradingPost(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildLumbermill)) != null)
                    GameController.lumberMill(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildPasture)) != null)
                    GameController.pasture(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildCamp)) != null)
                    GameController.camp(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildPlantation)) != null)
                    GameController.plantation(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildQuarry)) != null)
                    GameController.quarry(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.removeJungle)) != null)
                    GameController.removeJungle(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.removeRoute)) != null)
                    GameController.removeRoute(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.repair)) != null)
                    GameController.repair(); //TODO

                /*map*/
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapShow)) != null)
                    GameController.mapShow(); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveRight)) != null)
                    GameController.mapMoveRight(command); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveLeft)) != null)
                    GameController.mapMoveLeft(command); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveUp)) != null)
                    GameController.mapMoveUp(command); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveDown)) != null)
                    GameController.mapMoveDown(command); //TODO

                /*others*/
                else if(gameEnum.compareRegex(command, gameEnum.next) != null)
                {
                     GameController.changeTurn();
                     break;
                }
                else
                    System.out.println(mainCommands.invalidCommand.regex);
            }
        }
    }

    public static void run()
    {
        String command;
        Scanner scanner = new Scanner(System.in);
        Matcher matcher;

        while(scanner.hasNextLine())
        {
            command = scanner.nextLine();
            if((matcher = gameEnum.compareRegex(command, gameEnum.startGame)) != null)
            {
                HashMap<String, String> players = new HashMap<>();
                System.out.println(GameController.startNewGame(command, players));
                if(GameController.startNewGame(command, players).equals(gameEnum.successfulStartGame.regex))
                    startGame(scanner, players);
            }
            else if((matcher = mainCommands.compareRegex(command, mainCommands.menuExit)) != null)
                break;
            else if((matcher = mainCommands.compareRegex(command, mainCommands.enterMenu)) != null)
            {
                if(GameController.enterMenu(scanner, matcher).equals("1"))
                    break;
                else
                    System.out.println(GameController.enterMenu(scanner, matcher));
            }
            else if((matcher = mainCommands.compareRegex(command, mainCommands.showCurrentMenu)) != null)
                System.out.println(gameEnum.currentMenu.regex);
            else
                System.out.println(mainCommands.invalidCommand.regex);

        }
    }
}