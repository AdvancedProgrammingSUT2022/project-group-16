package Views;

import Controllers.GameController;
import Models.City.City;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Units.NonCombatUnits.Settler;
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
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;

public class gameMenuView
{
    private static final GameController gameController = GameController.getInstance();
    private static void printSpace(int n)
    {
        for(int i = 0; i < n; i++)
            System.out.print(" ");
    }
    private static int numberOfDigits(int number)
    {
        int d = 0;
        if(number == 0) return 1;
        while (number > 0)
        {
            number /= 10;
            d++;
        }
        return d;
    }
    private static void showEconomics(Scanner scanner)
    {
        ArrayList<City> n = gameController.getPlayerTurn().getCities();
        int max = gameController.getPlayerTurn().getCities().size();
        if(n.size() != 0)
            System.out.println("city name\t\t\tpopulation\t\tpower force\t\tfood yield\t\tcup yield\t\tgold yield\t\tproduction yield");
        for (int i = 0; i < max; i++)
        {
            System.out.print(n.get(i).getName());
            printSpace(24 - n.get(i).getName().length());
            System.out.print(n.get(i).getPopulation());
            printSpace(17 - numberOfDigits(n.get(i).getPopulation()));
            System.out.print(n.get(i).getPower());
            printSpace(16 - numberOfDigits(n.get(i).getPower()));
            System.out.print(n.get(i).getFoodYield());
            printSpace(15 - numberOfDigits(n.get(i).getFoodYield()));
            System.out.print(n.get(i).getCupYield());
            printSpace(17 - numberOfDigits(n.get(i).getCupYield()));
            System.out.print(n.get(i).getGoldYield());
            printSpace(17 - numberOfDigits(n.get(i).getGoldYield()));
            System.out.println(n.get(i).getPopulation());
        }
        System.out.println("1: " + infoCommands.searchCity.regex);
        System.out.println("2: " + infoCommands.backToGame.regex);
        int number = 0;
        while (number == 0)
        {
            number = gameController.getNum(scanner, 1, 2);
            if (number == 0)
                System.out.println(mainCommands.pickBetween.regex + "1 and 2");
        }
        if(number == 1)
            showAllCities(scanner);

    }
    private static void showGainedTechnologies()
    {
        System.out.println(infoCommands.gained.regex);
        ArrayList<Technology> tmp = gameController.getPlayerTurn().getTechnologies();
        if(tmp.size() == 0)
            System.out.println(infoCommands.nothing.regex);
        else
            for (int i = 0; i < tmp.size(); i++)
                System.out.println((i + 1) + ": " + tmp.get(i).toString());
    }
    private static void showTechnologies(Scanner scanner)
    {
        showGainedTechnologies();
        System.out.println(infoCommands.choose.regex);
        int max = 0;
        ArrayList<Technology> n = new ArrayList<Technology>();
        for(int i = 0; i < Technology.values().length; i++)
            if(gameController.getPlayerTurn().getTechnologies().containsAll(Technology.values()[i].requiredTechnologies) &&
                !gameController.getPlayerTurn().getTechnologies().contains(Technology.values()[i]))
            {
                System.out.println((max + 1) + ": " + Technology.values()[i].toString());
                n.add(Technology.values()[i]);
                max++;
            }
        System.out.println((max + 1) + infoCommands.backToGame.regex);
        int number = 0;
        while (number == 0)
        {
            number = gameController.getNum(scanner, 1, max + 1);
            if (number == 0)
                System.out.println(mainCommands.pickBetween.regex + "1 and " + (max + 1));
        }
        if(number != max + 1)
            gameController.getPlayerTurn().addTechnology(n.get(number - 1));
    }
    private static void showAllCities(Scanner scanner)
    {
        int max = gameController.getPlayerTurn().getCities().size();
        for (int i = 0; i < max; i++)
            System.out.println((i + 1) + ": " + gameController.getPlayerTurn().getCities().get(i).getName());
        System.out.println((max + 1) + infoCommands.searchEconomic.regex);
        System.out.println((max + 2) + infoCommands.backToGame.regex);
        int number = 0;
        while (number == 0) {
            number = gameController.getNum(scanner, 1, max + 2);
            if (number == 0)
                System.out.println(mainCommands.pickBetween.regex + "1 and " + (max + 2));
        }
        if(number == max + 1)
            showEconomics(scanner);
        else if(number != max + 2)
        {
            gameController.getPlayerTurn().setSelectedCity(gameController.getPlayerTurn().getCities().get(number - 1));
            showCity();
        }
    }
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
    private static void pickCivilizations(Scanner scanner, User[] tmpUsers)
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
                System.out.println(gameController.pickCivilization(number));
            } while (number > 10 || number < 1 || gameController.inArr(gameController.findCivilByNumber(number)));

            gameController.addPlayer(new Player(gameController.findCivilByNumber(number), tmpUser.getUsername(),
                    tmpUser.getNickname(), tmpUser.getPassword()));
        }
    }
    public static void startGame(Scanner scanner, HashMap<String, String> Map)
    {
        Matcher matcher;
        User[] tmpUsers = gameController.convertMapToArr(Map); //Note: player[0] is loggedInUSer! [loggedInUser, player1, player2, ...]
        pickCivilizations(scanner, tmpUsers);
        gameController.initGame();

        String command = null;
        do
        {
            if(gameController.getPlayers().indexOf(gameController.getPlayerTurn()) == 0)
                gameController.addToTurnCounter(1);
            System.out.println(gameController.getPlayerTurn().getUsername() + gameEnum.turn.regex);

            while (scanner.hasNextLine()) {
                command = scanner.nextLine();

                /*cheat codes*/
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null) //done
                    System.out.println(gameController.increaseGold(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //done
                    System.out.println(gameController.increaseTurns(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null) //done
                    System.out.println(gameController.increaseFood(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null) //TODO
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
                    showAllCities(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoDemographics) != null)
                    System.out.println(gameController.showDemographics());
                else if(infoCommands.compareRegex(command, infoCommands.infoNotifications) != null)
                    System.out.println(gameController.showNotifications());
                else if(infoCommands.compareRegex(command, infoCommands.infoMilitary) != null)
                    System.out.println(gameController.showMilitary());
                else if(infoCommands.compareRegex(command, infoCommands.infoEconomic) != null)
                    showEconomics(scanner); //TODO: add current construction (and turns til end) later
                else if(infoCommands.compareRegex(command, infoCommands.infoTechnologies) != null)
                    showTechnologies(scanner);

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
        } while (!Objects.equals(command, gameEnum.end.toString()));
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