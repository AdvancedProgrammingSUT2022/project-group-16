package Views;

import Controllers.GameController;
import Controllers.Utilities.MapPrinter;
import Models.City.City;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Units.CombatUnits.*;
import Models.Units.NonCombatUnits.*;
import Models.Units.Unit;
import Models.User;
import enums.cheatCode;
import enums.gameCommands.*;
import enums.gameEnum;
import enums.mainCommands;

import java.util.*;
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
        if(number == 0)
            return 1;
        return (int)(Math.log10(number) + 1);
    }

    private static void showAllUnits(int max)
    {
        if(max != 0)
            System.out.println("unit type\t\tcoordinates\t\tpower\t\tMP\t\thealth\t\tis active\t\tis sleep");
        for (int i = 0; i < gameController.getPlayerTurn().getUnits().size(); i++)
        {
            Unit curr = gameController.getPlayerTurn().getUnits().get(i);
            System.out.print(" " + curr.toString().toLowerCase(Locale.ROOT));
            printSpace(19 - curr.toString().length());
            System.out.print(curr.getTile().getPosition().X + "," + curr.getTile().getPosition().Y);
            printSpace(11);
            System.out.print(curr.getPower());
            printSpace(10 - numberOfDigits(curr.getPower()));
            System.out.print(curr.getMovementPoints());
            printSpace(10 - numberOfDigits(curr.getMovementPoints()));
            System.out.print(curr.getHealth());
            printSpace(12 - numberOfDigits(curr.getHealth()));
            System.out.print(curr.isActive());
            if(curr.isActive()) printSpace(11);
            else printSpace(10);
            System.out.println(curr.isSleep());
        }
    }

    private static void showDemographic()
    {
        Player tmp = gameController.getPlayerTurn();
        System.out.println(infoCommands.civilizationName.regex + gameController.getPlayerTurn().getCivilization().name() + "\n");
        System.out.println(infoCommands.cities.regex);
        printCities(tmp.getCities().size());
        System.out.println();
        System.out.println(gameEnum.population.regex + tmp.getPopulation());
        System.out.println(gameEnum.happiness.regex + tmp.getHappiness());
//        System.out.println(tmp.get); //TODO: resource
        System.out.println(gameEnum.food.regex + tmp.getFood());
        System.out.println(gameEnum.cup.regex + tmp.getCup());
        System.out.println(gameEnum.cupIncome.regex + tmp.incomeCup());
        System.out.println(gameEnum.gold.regex + tmp.getGold());
        System.out.println(gameEnum.goldIncome.regex + tmp.incomeGold());

        if(gameController.getPlayerTurn().getUnits().size() != 0)
            System.out.println(infoCommands.units.regex);
        showAllUnits(gameController.getPlayerTurn().getUnits().size());
    }
    private static void showMilitary()
    {
        if (gameController.getPlayerTurn().getUnits().size() == 0)
            System.out.println("you have not any unit");
        else
            showAllUnits(gameController.getPlayerTurn().getUnits().size());
    }
    private static void showUnits(Scanner scanner)
    {
        ArrayList<Unit> tmp = gameController.getPlayerTurn().getUnits();
        int max = gameController.getPlayerTurn().getUnits().size(), number = 0;
        if(max != 0)
            System.out.println("choose unit number to change active/inactive");
        while (number != max + 1)
        {
            number = 0;
            if(max != 0)
                System.out.println("   unit type\t\tcoordinates\t\tis active");
            for (int i = 0; i < max; i++)
            {
                Unit curr = gameController.getPlayerTurn().getUnits().get(i);
                System.out.print((i + 1) + ": " + curr.toString().toLowerCase(Locale.ROOT));
                printSpace(20 - curr.toString().length());
                System.out.print(curr.getTile().getPosition().X + "," + curr.getTile().getPosition().Y);
                if(curr.isActive()) System.out.println("\t\t\t Active");
                else System.out.println("\t\t\tIn Active");
            }
            System.out.println((max + 1) + ": go to Military panel");
            System.out.println((max + 2) + infoCommands.backToGame.regex);
            while (number == 0) {
                number = gameController.getNum(scanner, 1, max + 2);
                if (number == 0)
                    System.out.println(mainCommands.pickBetween.regex + "1 and " + (max + 2));
            }
            if (number == max + 1)
                showMilitary();
            else if(number == max + 2)
                number = max + 1;
            else
                tmp.get(number - 1).changeActivate();
        }
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
        System.out.println();
    }
    private static void showTechnologies(Scanner scanner)
    {
        System.out.println(infoCommands.numberOfCup.regex + gameController.getPlayerTurn().getCup());
        showGainedTechnologies();
        System.out.println(infoCommands.chooseTechnology.regex);
        int max = 0, flag = -1;
        Player tmp = gameController.getPlayerTurn();
        ArrayList<Technology> candidateTechs = new ArrayList<>();
        for(int i = 0; i < Technology.values().length; i++)
            if(tmp.getTechnologies().containsAll(Technology.values()[i].requiredTechnologies) &&
                !tmp.getTechnologies().contains(Technology.values()[i]))
            {
                if(tmp.getResearchingTechnology() != null &&
                    Technology.values()[i].equals(tmp.getResearchingTechnology()))
                {
                    System.out.println((max + 1) + ": " + Technology.values()[i].toString() + infoCommands.currResearch.regex);
                    flag = max + 1;
                }
                else
                {
                    System.out.println((max + 1) + ": " + Technology.values()[i].toString() + infoCommands.requiredTurns.regex +
                            (Technology.values()[i].cost - tmp.getResearchingTechCounter()[i]));
                    if(gameController.requiredTechForBuilding(Technology.values()[i]) != null)
                        System.out.println(infoCommands.willGain.regex + gameController.requiredTechForBuilding(Technology.values()[i]).name());
                    if(gameController.requiredTechForImprovement(Technology.values()[i]) != null)
                        System.out.println(infoCommands.willGain.regex + gameController.requiredTechForImprovement(Technology.values()[i]).name());
                }
                candidateTechs.add(Technology.values()[i]);
                max++;
            }
        System.out.println((max + 1) + infoCommands.backToGame.regex);
        int number = 0;
        while (number == 0)
        {
            number = gameController.getNum(scanner, 1, max + 1);
            if (number == 0) System.out.println(mainCommands.pickBetween.regex + "1 and " + (max + 1));
        }
        int flg = -1;
        if(number != max + 1)
            for(int i = 0; i < Technology.values().length; i++)
                if(Technology.values()[i] == candidateTechs.get(number - 1)) flg = i;
        if(number == flag)
            System.out.println(infoCommands.alreadyResearching.regex);
        else if(number != max + 1 && tmp.getCup() >= candidateTechs.get(number - 1).cost - tmp.getResearchingTechCounter()[flg])
        {
            tmp.setResearchingTechnology(candidateTechs.get(number - 1));
            System.out.println(infoCommands.choose.regex + candidateTechs.get(number - 1).name() + infoCommands.successful.regex);
            tmp.reduceCup();
        }
        else if(number != max + 1)
            System.out.println(infoCommands.enoughCup.regex + candidateTechs.get(number - 1).name());
    }
    private static void printCities(int max)
    {
        if(max == 0)
            System.out.println(infoCommands.nothing.regex);
        else
        {
            System.out.println(1 + ": " + gameController.getPlayerTurn().getCurrentCapitalCity().getName() + " (capital city)");
            for (int i = 1; i < max; i++)
                System.out.println((i + 1) + ": " + gameController.getPlayerTurn().getCities().get(i).getName());
        }
    }
    private static void showAllCities(Scanner scanner)
    {
        int max = gameController.getPlayerTurn().getCities().size();
        printCities(max);
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
        Unit tmp = gameController.getPlayerTurn().getSelectedUnit();

        System.out.print(infoCommands.unitType.regex + tmp.getClass().getSuperclass().getSimpleName());
        if(tmp.getClass().equals(LongRange.class))
            System.out.println(" - " + tmp.getClass().getSimpleName() + infoCommands.name.regex +
                    ((LongRange) tmp).getType().name().toLowerCase(Locale.ROOT));
        else if(tmp.getClass().equals(MidRange.class))
            System.out.println(" - " + tmp.getClass().getSimpleName() + infoCommands.name.regex +
                    ((MidRange) tmp).getType().name().toLowerCase(Locale.ROOT));
        else
            System.out.println(infoCommands.name.regex + tmp.getClass().getSimpleName().toLowerCase(Locale.ROOT));
        System.out.println(gameEnum.productionCost.regex + tmp.getProductionCost());
        System.out.println(gameEnum.speed.regex + tmp.getSpeed());
        System.out.println(gameEnum.power.regex + tmp.getPower());
        System.out.println(gameEnum.health.regex + tmp.getHealth());
    }
    private static void showCity()
    {
        City tmp = gameController.getPlayerTurn().getSelectedCity();

        System.out.println(infoCommands.cityName.regex + tmp.getName());
        System.out.println(gameEnum.foodYield.regex + gameController.getPlayerTurn().getSelectedCity().getFoodYield());
        System.out.println(gameEnum.production.regex + gameController.getPlayerTurn().getSelectedCity().getProductionYield());
        System.out.println(gameEnum.goldYield.regex + gameController.getPlayerTurn().getSelectedCity().getGoldYield());
        System.out.println(gameEnum.cupYield.regex + gameController.getPlayerTurn().getSelectedCity().getCupYield());
    }
    private static void pickCivilsAndCreatePlayers(Scanner scanner, User[] usersToPlay) // and create players
    {
        for (User user : usersToPlay)
        {
            //TODO: this should be fixed: printing civilizations should be dynamic
            System.out.println(user.getUsername() + gameEnum.pickCivilization.regex);
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
                    number = gameController.getNum(scanner, 1, 10); //TODO: shouldn't be in GameController
                    if (number == 0)
                        System.out.println(mainCommands.pickBetween.regex + "1 and 10");
                }
                System.out.println(gameController.pickCivilization(number));
            } while (number > 10 || number < 1 || gameController.inArr(gameController.findCivilByNumber(number)));

            gameController.addPlayer(new Player(gameController.findCivilByNumber(number), user.getUsername(),
                    user.getNickname(), user.getPassword()));
        }
    }
    public static void startGame(Scanner scanner, HashMap<String, String> usersInfo) //TODO: rename to runGame
    {
        Matcher matcher;
        User[] tmpUsers = gameController.convertMapToArr(usersInfo); //Note: user[0] is loggedInUSer! [loggedInUser, user1, user2, ...]
        pickCivilsAndCreatePlayers(scanner, tmpUsers);
        gameController.initGame();

        String command = null;
        do
        {
            gameController.getPlayerTurn().setCup(gameController.getPlayerTurn().incomeCup());
            if(gameController.getPlayers().indexOf(gameController.getPlayerTurn()) == 0)
            {
                gameController.addTurn(1);
                gameController.addToTurnCounter(1);
            }
            System.out.println(gameController.getPlayerTurn().getUsername() + gameEnum.turn.regex);
            System.out.println(MapPrinter.getMapString(gameController.getPlayerTurn()));
            String e = gameController.checkTechnology();
            if(e != null) System.out.println(e);

            while (scanner.hasNextLine())
            {
                command = scanner.nextLine();
                String s = gameController.checkTechnology();
                if(s != null) System.out.println(s);

                //update tileStates for playerTurn
                gameController.getPlayerTurn().updateTileStates();

                /*cheat codes*/
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null)
                    System.out.println(gameController.increaseGold(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null)
                        System.out.println(gameController.increaseTurns(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null)
                    System.out.println(gameController.increaseFood(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null)
                    System.out.println(gameController.addTechnology(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseHappiness)) != null)
                    System.out.println(gameController.increaseHappiness(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.winBattle)) != null) //TODO
                    System.out.println(gameController.winBattle(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null) //TODO
                    System.out.println(gameController.moveUnit(matcher));

                /*Info*/
                else if(infoCommands.compareRegex(command, infoCommands.infoResearch) != null)
                    System.out.println(gameController.showResearch());
                else if(infoCommands.compareRegex(command, infoCommands.infoUnits) != null)
                    showUnits(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoCities) != null)
                    showAllCities(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoDemographics) != null) //TODO
                    showDemographic();
                else if(infoCommands.compareRegex(command, infoCommands.infoNotifications) != null) //TODO
                    System.out.println(gameController.showNotifications());
                else if(infoCommands.compareRegex(command, infoCommands.infoMilitary) != null)
                    showMilitary();
                else if(infoCommands.compareRegex(command, infoCommands.infoEconomic) != null) //TODO: add current construction (and turns til end) later
                    showEconomics(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoTechnologies) != null)
                    showTechnologies(scanner);

                /*Select*/
                else if(selectCommands.compareRegex(command, selectCommands.selectCombat) != null)
                {
                    System.out.println(gameController.selectCUnit(command));
                    if(gameController.selectCUnit(command).equals(selectCommands.selected.regex))
                        showUnit();
                }
                else if(selectCommands.compareRegex(command, selectCommands.selectNonCombat) != null)
                {
                    System.out.println(gameController.selectNUnit(command));
                    if(gameController.selectNUnit(command).equals(selectCommands.selected.regex))
                        showUnit();
                }
                else if(selectCommands.compareRegex(command, selectCommands.selectCity) != null)
                {
                    System.out.println(gameController.selectCity(command));
                    if(gameController.getPlayerTurn().getSelectedCity() != null)
                        showCity();
                }

                /*unit*/
                else if((matcher = unitCommands.compareRegex(command, unitCommands.moveTo)) != null)
                    gameController.moveUnit(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.sleep) != null)
                {
                    System.out.println(gameController.sleep());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
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
                    gameController.found(); //TODO: update cups
                else if(unitCommands.compareRegex(command, unitCommands.cancelMission) != null)
                    gameController.cancel(); //TODO
                else if(unitCommands.compareRegex(command, unitCommands.wake) != null)
                {
                    System.out.println(gameController.wake());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
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
                else if(command.equals("s"))
                {
                    MidRange z = new MidRange(gameController.getPlayerTurn(), MidRangeType.HORSEMAN, gameController.getMap().get(45),12, 34);
                    Settler n = new Settler(gameController.getPlayerTurn(),gameController.getMap().get(0));
                    Worker w = new Worker(gameController.getPlayerTurn(),gameController.getMap().get(23));
                    LongRange q = new LongRange(gameController.getPlayerTurn(), LongRangeType.CATAPULT, gameController.getMap().get(34),10, 23);
                    Settler m = new Settler(gameController.getPlayerTurn(),gameController.getMap().get(1));
                    MidRange o = new MidRange(gameController.getPlayerTurn(), MidRangeType.CAVALRY, gameController.getMap().get(45),12, 34);
                    Worker k = new Worker(gameController.getPlayerTurn(),gameController.getMap().get(2));
                    LongRange r = new LongRange(gameController.getPlayerTurn(), LongRangeType.ARTILLERY, gameController.getMap().get(34),10, 23);
                    LongRange l = new LongRange(gameController.getPlayerTurn(), LongRangeType.ARCHER, gameController.getMap().get(34),10, 23);
                    n.createCity();
                    gameController.getPlayerTurn().setCapitalCity(gameController.getPlayerTurn().getCities().get(0));
                    m.createCity();
                    gameController.getPlayerTurn().getCities().get(1).addPopulation(12);
                    gameController.getPlayerTurn().getCities().get(0).addPopulation(7);


                }
                else if(command.equals("t"))
                {
//                    Settler n = new Settler(gameController.getPlayerTurn(), 10,10,gameController.getMap().get(0),10,5);
                    Settler n = new Settler(gameController.getPlayerTurn(), gameController.getMap().get(0));
                    n.createCity();
                    gameController.getPlayerTurn().getCities().get(2).addPopulation(34);
                    gameController.getPlayerTurn().getCities().get(2).addGold(24);
                }
                else if(gameEnum.compareRegex(command, gameEnum.next) != null)
                {
                    String changeTurnResult = gameController.checkChangeTurn();
                    if(changeTurnResult != null)
                        System.out.println(changeTurnResult);
                    else // change turn was successful
                        break;
                }
                else
                    System.out.println(mainCommands.invalidCommand.regex);
                String t = gameController.checkTechnology();
                if(t != null) System.out.println(t);
            }
        } while (!Objects.equals(command, gameEnum.end.toString())) ;{
            gameController.updatePlayersUnitLocations();
        }
    }
    public static void run() //TODO: rename to runGameMenu
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