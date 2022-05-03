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
    private static void showCity(int number, GameController gameController)
    {
        System.out.println(gameEnum.food.regex + gameController.getPlayerTurn().getCities().get(number).getFoodYield());
        System.out.println(gameEnum.production.regex + gameController.getPlayerTurn().getCities().get(number).getProductionYield());
        System.out.println(gameEnum.gold.regex + gameController.getPlayerTurn().getCities().get(number).getGoldYield());
        System.out.println(gameEnum.cup.regex + gameController.getPlayerTurn().getCities().get(number).getCupYield());
    }

    private static void pickCivilizations(Scanner scanner, User[] tmpUsers, ArrayList<Player> players, GameController gameController)
    {
        for (User tmpUser : tmpUsers)
        {
            System.out.println(tmpUser.getUsername() + gameEnum.pickCivilization.regex);
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
            do {
                number = 0;
                while (number == 0)
                {
                    number = gameController.getNum(scanner, 1, 10);
                    if (number == 0)
                        System.out.println(mainCommands.pickBetween.regex + "1 and 10");
                }
                System.out.println(gameController.pickCivilization(players, number));
            } while (number > 10 || number < 1 || gameController.inArr(gameController.findCivilByNumber(number)));

            gameController.addPlayer(new Player(gameController.findCivilByNumber(number), tmpUser.getUsername(),
                    tmpUser.getNickname(), tmpUser.getPassword()));
        }
    }

    public static void startGame(Scanner scanner, HashMap<String, String> Map)
    {
        GameController gameController = GameController.getInstance();
        Matcher matcher;
        User[] tmpUsers = gameController.convertMapToArr(Map); //Note: player[0] is loggedInUSer! [loggedInUser, player1, player2, ...]
        ArrayList<Player> players = new ArrayList<>();
        pickCivilizations(scanner, tmpUsers, players, gameController);
        gameController.setFirstPlayer();

        while (true)
        {
            System.out.println(gameController.getPlayerTurn().getUsername() + gameEnum.turn.regex);

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();

                /*cheat codes*/
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null) //TODO
                    System.out.println(gameController.increaseGold(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //TODO
                    System.out.println(gameController.increaseTurns(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null) //done
                    System.out.println(gameController.increaseFood(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null) //done
                    System.out.println(gameController.addTechnology(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.winBattle)) != null) //TODO
                    System.out.println(gameController.winBattle(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null) //TODO
                    System.out.println(gameController.moveUnit(matcher));

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
                    String tmp = gameController.selectCUnit(command);
                    if(gameController.isValid(tmp))
                    {
                        if(Integer.parseInt(tmp) == -1)
                            System.out.println(mainCommands.invalidCommand);
                        else
                        {
                            //TODO: show combat unit information
                            gameController.getPlayerTurn().setSelectedUnit(gameController.getPlayerTurn().getUnits().get(Integer.parseInt(tmp)));
                        }
                    }
                    else
                        System.out.println(tmp);
                }
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectNonCombat)) != null)
                {
                    String tmp = gameController.selectNUnit(command);
                    if(gameController.isValid(tmp))
                    {
                        if(Integer.parseInt(tmp) == -1)
                            System.out.println(mainCommands.invalidCommand);
                        else
                        {
                            //TODO: show nonCombat unit information
                            gameController.getPlayerTurn().setSelectedUnit(gameController.getPlayerTurn().getUnits().get(Integer.parseInt(tmp)));
                        }
                    }
                    else
                        System.out.println(tmp);
                }
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectCity)) != null)
                {
                    String tmp = gameController.selectCity(command);
                    if(gameController.isValid(tmp))
                    {
                        if(Integer.parseInt(tmp) == -1)
                            System.out.println(mainCommands.invalidCommand);
                        else
                        {
                            showCity(Integer.parseInt(tmp), gameController);
                            gameController.getPlayerTurn().setSelectedCity(gameController.getPlayerTurn().getCities().get(Integer.parseInt(tmp)));
                        }
                    }
                    else
                        System.out.println(tmp);
                }

                /*unit*/
                else if((matcher = unitCommands.compareRegex(command, unitCommands.moveTo)) != null)
                    gameController.moveUnit(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.sleep)) != null)
                    gameController.sleep(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.alert)) != null)
                    gameController.alert(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.fortify)) != null)
                    gameController.fortify(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.fortifyHeal)) != null)
                    gameController.fortifyTilHeal(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.garrison)) != null)
                    gameController.garrison(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.setup)) != null)
                    gameController.setup(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.attack)) != null)
                    gameController.attack(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.foundCity)) != null)
                    gameController.found(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.cancelMission)) != null)
                    gameController.cancel(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.wake)) != null)
                    gameController.wake(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.delete)) != null)
                    gameController.delete(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildRoad)) != null)
                    gameController.road(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildRailRoad)) != null)
                    gameController.railRoad(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildFarm)) != null)
                    gameController.farm(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildMine)) != null)
                    gameController.mine(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildTradingPost)) != null)
                    gameController.tradingPost(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildLumbermill)) != null)
                    gameController.lumberMill(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildPasture)) != null)
                    gameController.pasture(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildCamp)) != null)
                    gameController.camp(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildPlantation)) != null)
                    gameController.plantation(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.buildQuarry)) != null)
                    gameController.quarry(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.removeJungle)) != null)
                    gameController.removeJungle(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.removeRoute)) != null)
                    gameController.removeRoute(); //TODO
                else if((matcher = unitCommands.compareRegex(command, unitCommands.repair)) != null)
                    gameController.repair(); //TODO

                /*map*/
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapShow)) != null)
                    gameController.mapShow(); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveRight)) != null)
                    gameController.mapMoveRight(command); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveLeft)) != null)
                    gameController.mapMoveLeft(command); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveUp)) != null)
                    gameController.mapMoveUp(command); //TODO
                else if((matcher = mapCommands.compareRegex(command, mapCommands.mapMoveDown)) != null)
                    gameController.mapMoveDown(command); //TODO

                /*others*/
                else if(gameEnum.compareRegex(command, gameEnum.next) != null)
                {
                    // TODO: check for the error and print it
                    gameController.changeTurn();
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