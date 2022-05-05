package Views;

import Controllers.GameController;
import Models.Player.Player;
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
    private static GameController gameController = GameController.getInstance();

    private static void showUnit()
    {
        System.out.println(gameEnum.speed.regex + gameController.getPlayerTurn().getSelectedUnit().getSpeed());
        System.out.println(gameEnum.power.regex + gameController.getPlayerTurn().getSelectedUnit().getPower());
        System.out.println(gameEnum.health.regex + gameController.getPlayerTurn().getSelectedUnit().getHealth());
        //TODO: maybe need edit
    }
    private static void showCity()
    {
        System.out.println(gameEnum.food.regex + gameController.getPlayerTurn().getSelectedCity().getFoodYield());
        System.out.println(gameEnum.production.regex + gameController.getPlayerTurn().getSelectedCity().getProductionYield());
        System.out.println(gameEnum.gold.regex + gameController.getPlayerTurn().getSelectedCity().getGoldYield());
        System.out.println(gameEnum.cup.regex + gameController.getPlayerTurn().getSelectedCity().getCupYield());
    }
    private static void pickCivilizations(Scanner scanner, User[] tmpUsers, ArrayList<Player> players)
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
        Matcher matcher;
        User[] tmpUsers = gameController.convertMapToArr(Map); //Note: player[0] is loggedInUSer! [loggedInUser, player1, player2, ...]
        ArrayList<Player> players = new ArrayList<>();
        pickCivilizations(scanner, tmpUsers, players);
        gameController.initGame();

        String command = null;

        do
        {
            System.out.println(gameController.getPlayerTurn().getUsername() + gameEnum.turn.regex);

            while (scanner.hasNextLine()) {
                command = scanner.nextLine();

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
                else if(infoCommands.compareRegex(command, infoCommands.infoResearch) != null)
                    System.out.println(gameController.showResearch());
                else if(infoCommands.compareRegex(command, infoCommands.infoUnits) != null)
                    System.out.println(gameController.showUnits());
                else if(infoCommands.compareRegex(command, infoCommands.infoCities) != null)
                    System.out.println(gameController.showCities());
                else if(infoCommands.compareRegex(command, infoCommands.infoDiplomacy) != null)
                    System.out.println(gameController.showDiplomacy());
                else if(infoCommands.compareRegex(command, infoCommands.infoVictory) != null)
                    System.out.println(gameController.showVictory());
                else if(infoCommands.compareRegex(command, infoCommands.infoDemographics) != null)
                    System.out.println(gameController.showDemographics());
                else if(infoCommands.compareRegex(command, infoCommands.infoNotifications) != null)
                    System.out.println(gameController.showNotifications());
                else if(infoCommands.compareRegex(command, infoCommands.infoMilitary) != null)
                    System.out.println(gameController.showMilitary());
                else if(infoCommands.compareRegex(command, infoCommands.infoEconomic) != null)
                    System.out.println(gameController.showEconomics());
                else if(infoCommands.compareRegex(command, infoCommands.infoDiplomatic) != null)
                    System.out.println(gameController.showDiplomatic());
                else if(infoCommands.compareRegex(command, infoCommands.infoDeals) != null)
                    System.out.println(gameController.showDeals());

                /*Select*/
                else if(selectCommands.compareRegex(command, selectCommands.selectCombat) != null)
                {
                    System.out.println(gameController.selectCUnit(command));
                    if(gameController.getPlayerTurn().getSelectedUnit() != null)
                        showUnit();
                }
                else if(selectCommands.compareRegex(command, selectCommands.selectNonCombat) != null)
                {
                    System.out.println(gameController.selectNUnit(command));
                    if(gameController.getPlayerTurn().getSelectedUnit() != null)
                        showUnit();
                }
                else if(selectCommands.compareRegex(command, selectCommands.selectCity) != null)
                {
                    System.out.println(gameController.selectCity(command));
                    if(gameController.getPlayerTurn().getSelectedCity() != null)
                        showCity();
                }

                /*unit*/
                else if(unitCommands.compareRegex(command, unitCommands.moveTo) != null)
                    gameController.moveUnit(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.sleep) != null)
                    gameController.sleep(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.alert) != null)
                    gameController.alert(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.fortify) != null)
                    gameController.fortify(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.fortifyHeal) != null)
                    gameController.fortifyTilHeal(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.garrison) != null)
                    gameController.garrison(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.setup) != null)
                    gameController.setup(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.attack) != null)
                    gameController.attack(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.foundCity) != null)
                    gameController.found(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.cancelMission) != null)
                    gameController.cancel(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.wake) != null)
                    gameController.wake(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.delete) != null)
                    gameController.delete(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildRoad) != null)
                    gameController.road(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildRailRoad) != null)
                    gameController.railRoad(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildFarm) != null)
                    gameController.farm(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildMine) != null)
                    gameController.mine(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildTradingPost) != null)
                    gameController.tradingPost(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildLumbermill) != null)
                    gameController.lumberMill(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildPasture) != null)
                    gameController.pasture(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildCamp) != null)
                    gameController.camp(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildPlantation) != null)
                    gameController.plantation(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.buildQuarry) != null)
                    gameController.quarry(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.removeJungle) != null)
                    gameController.removeJungle(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.removeRoute) != null)
                    gameController.removeRoute(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.repair) != null)
                    gameController.repair(); //TODO

                /*map*/
                else if(mapCommands.compareRegex(command, mapCommands.mapShow) != null)
                    System.out.println(gameController.mapShow(command)); //TODO
                else if(mapCommands.compareRegex(command, mapCommands.mapMoveRight) != null)
                    System.out.println(gameController.mapMoveRight(command)); //TODO
                else if(mapCommands.compareRegex(command, mapCommands.mapMoveLeft) != null)
                    System.out.println(gameController.mapMoveLeft(command)); //TODO
                else if(mapCommands.compareRegex(command, mapCommands.mapMoveUp) != null)
                    System.out.println(gameController.mapMoveUp(command)); //TODO
                else if(mapCommands.compareRegex(command, mapCommands.mapMoveDown) != null)
                    System.out.println(gameController.mapMoveDown(command)); //TODO

                /*others*/
                else if(gameEnum.compareRegex(command, gameEnum.end) != null)
                {
                    gameController.removeAllPlayers();
                    System.out.println(gameEnum.endGame.regex);
                    break;
                } //end game
                else if(gameEnum.compareRegex(command, gameEnum.next) != null)
                {
                    // TODO: check for the error and print it
                    gameController.changeTurn();
                    break;
                }
                else
                    System.out.println(mainCommands.invalidCommand.regex);
            }
        } while (!command.equals(gameEnum.end.toString()));
    }
    public static void run()
    {
        String command;
        Scanner scanner = new Scanner(System.in);
        Matcher matcher;

        while(scanner.hasNextLine())
        {
            command = scanner.nextLine();
            if(gameEnum.compareRegex(command, gameEnum.startGame) != null)
            {
                HashMap<String, String> players = new HashMap<>();
                System.out.println(gameController.startNewGame(command, players));
                if(gameController.startNewGame(command, players).equals(gameEnum.successfulStartGame.regex))
                    startGame(scanner, players);
            }
            else if(mainCommands.compareRegex(command, mainCommands.menuExit) != null)
                break;
            else if((matcher = mainCommands.compareRegex(command, mainCommands.enterMenu)) != null)
            {
                if(GameController.enterMenu(scanner, matcher).equals("1"))
                    break;
                else
                    System.out.println(GameController.enterMenu(scanner, matcher));
            }
            else if(mainCommands.compareRegex(command, mainCommands.showCurrentMenu) != null)
                System.out.println(gameEnum.currentMenu.regex);
            else if(command != null)
                System.out.println(mainCommands.invalidCommand.regex);

        }
    }
}