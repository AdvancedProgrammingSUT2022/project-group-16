package Views;

import Controllers.GameController;
import Controllers.Utilities.MapPrinter;
import Models.City.*;
import Models.City.BuildingType;
import Models.City.City;
import Models.Player.Civilization;
import Models.Player.Notification;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.LuxuryResource;
import Models.Resources.ResourceType;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.*;
import Models.Units.NonCombatUnits.*;
import Models.Units.Unit;
import Models.Units.UnitState;
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
    private static int getNumber(Scanner scanner, int max)
    {
        int tmp = 0;
        while (tmp == 0)
        {
            tmp = gameController.getNum(scanner,1,max);
            if(tmp == 0)
                System.out.println(mainCommands.pickBetween.regex + 1 + mainCommands.and.regex + max);
        }
        return tmp;
    }

    private static int cityDestroy(Scanner scanner)
    {
        System.out.println(infoCommands.cityGot.regex);
        System.out.println(infoCommands.destroy.regex);
        System.out.println(infoCommands.attach.regex);
        return getNumber(scanner, 2);
    }
    private static String buyUnit(Scanner scanner)
    {
        MidRangeType tmp1 = null;
        LongRangeType tmp2 = null;
        System.out.println("1: mid range type");
        System.out.println("2: long range type");
        System.out.println("3: Settler - 89");
        System.out.println("4: Worker - 70");
        System.out.println("5: " + infoCommands.backToGame.regex);
        int number = getNumber(scanner, 5);
        if(number == 1) tmp1 = showValidMidrange(scanner);
        if(number == 2) tmp2 = showValidLongranges(scanner);
        if(number == 3) return "SETTLER";
        if(number == 4) return "WORKER";
        if(tmp1 != null)
            return tmp1.name();
        if(tmp2 != null)
            return tmp2.name();
        return null;
    }
    private static MidRangeType showValidMidrange(Scanner scanner)
    {
        int max = 0;
        ArrayList<MidRangeType> midType = new ArrayList<>();
        for(int i = 0; i < MidRangeType.values().length; i++)
        {
            if(gameController.validMidRange(MidRangeType.values()[i]))
            {
                System.out.println((max + 1) + ": " + MidRangeType.values()[i].name() + " - " + MidRangeType.values()[i].cost);
                midType.add(MidRangeType.values()[i]);
                max++;
            }
        }
        System.out.println((max + 1) + ": " + infoCommands.backToGame.regex);
        int number = getNumber(scanner, max + 1);
        if(number != max + 1)
            return midType.get(number - 1);
        return null;
    }
    private static LongRangeType showValidLongranges(Scanner scanner)
    {
        int max = 0;
        ArrayList<LongRangeType> longType = new ArrayList<>();
        for(int i = 0; i < LongRangeType.values().length; i++)
        {
            if(gameController.validLongRange(LongRangeType.values()[i]))
            {
                System.out.println((max + 1) + ": " + LongRangeType.values()[i].name() + " - " + LongRangeType.values()[i].cost);
                longType.add(LongRangeType.values()[i]);
                max++;
            }
        }
        System.out.println((max + 1) + ": " + infoCommands.backToGame.regex);
        int number = getNumber(scanner, max + 1);
        if(number != max + 1)
            return longType.get(number - 1);
        return null;
    }
    private static BuildingType showValidBuildings(Scanner scanner)
    {
        int max = 0;
        ArrayList<BuildingType> tmp = new ArrayList<>();
        for(int i = 0; i < BuildingType.values().length; i++)
            if(gameController.getPlayerTurn().getTechnologies().contains(BuildingType.values()[i].requiredTechnology)) {
                System.out.println((max + 1) + ": " + BuildingType.values()[i].name().toLowerCase(Locale.ROOT));
                tmp.add(BuildingType.values()[i]);
                max++;
            }
        System.out.println((max + 1) + ": " + infoCommands.backToGame.regex);
        int number = getNumber(scanner, max + 1);
        if(number != max + 1)
            return tmp.get(number - 1);
        return null;
    }
    // this method prints some information about the game like map and food and...
    public static void showBaseFields()
    {
        Player tmp = gameController.getPlayerTurn();
        System.out.println(tmp.getUsername() + gameEnum.turn.regex);
        System.out.println(MapPrinter.getMapString(tmp));
        System.out.println(tmp.getUsername() + gameEnum.turn.regex);
        System.out.print(gameEnum.gold.regex + tmp.getGold() + "\t\t\t" + gameEnum.happiness.regex +
                tmp.getHappiness());
        System.out.print(":");
        if(tmp.getHappiness() > 50)
            for(int i = 0; i < (tmp.getHappiness() - 50) / 10; i++)
                System.out.print(")");
        else
            for(int i = 0; i <  5 - (tmp.getHappiness() / 10); i++)
                System.out.print("(");
        System.out.println("\t\t\t" + gameEnum.food.regex + tmp.getFood() + "\t\t\t" + gameEnum.population.regex + tmp.getPopulation());
    }
    private static void showNotifications(Scanner scanner)
    {
        ArrayList<Notification> tmp = gameController.getPlayerTurn().getNotifications();
        int number = gameController.getPlayerTurn().getNotifications().size();
        if(number == 0)
            System.out.println(infoCommands.nothing.regex);
        for(int i = 0; i < number; i++)
        {
            System.out.print((i + 1) + ":\n\t");
            System.out.println(tmp.get(i).getMessage() + "\n\t" + infoCommands.sendMessage.regex + tmp.get(i).getSendingTurn());
        }
        System.out.println("\n1: " + infoCommands.backToGame.regex);
        getNumber(scanner, 1);
    }
    private static void showScoreBoard(Scanner scanner, Player player)
    {
        System.out.println(infoCommands.scoreBoard.regex);
        gameController.getPlayers().sort((o1, o2) -> {
            if (o1.getScore() == o2.getScore())
                return 0;
            return o1.getScore() < o2.getScore() ? -1 : 1;
        });
        int number = gameController.getPlayers().size();
        for(int i = number - 1; i >= 0; i--)
            System.out.println((number - i) + " - " + gameController.getPlayers().get(i).getCivilization().name().toLowerCase(Locale.ROOT)
             + ": " +  gameController.getPlayers().get(i).getScore());
        int avg = 0;
        for(Player player1 : gameController.getPlayers())
            avg += player1.getScore();
        System.out.println("----------------------");
        System.out.println(infoCommands.averageScore.regex + ((double) avg / gameController.getPlayers().size()));
        System.out.println("1: " + infoCommands.backToGame.regex);
        getNumber(scanner, 1);
        showDemographic(player, scanner);
    }
    private static void showAllUnits(Player player)
    {
        int max = player.getUnits().size();
        if(max != 0)
            System.out.println("unit type\t\tcoordinates\t\tpower\t\tMP\t\thealth\t\tunit state");
        for (int i = 0; i < max; i++)
        {
            Unit unit = player.getUnits().get(i);
            System.out.print(" " + unit.toString().toLowerCase(Locale.ROOT));
            printSpace(19 - unit.toString().length());
            System.out.print(unit.getTile().getPosition().X + "," + unit.getTile().getPosition().Y);
            printSpace(11);
            System.out.print(unit.getPower());
            printSpace(10 - numberOfDigits(unit.getPower()));
            System.out.print(unit.getMovementPoints());
            printSpace(10 - numberOfDigits(unit.getMovementPoints()));
            System.out.print(unit.getHealth());
            printSpace(14 - numberOfDigits(unit.getHealth()));
            System.out.println(unit.getUnitState().symbol);
        }
    }
    private static void showDemographic(Player tmp, Scanner scanner)
    {
        System.out.println(infoCommands.civilizationName.regex + tmp.getCivilization().name() + "\n");
        System.out.println(infoCommands.cities.regex);
        printCities(tmp);
        System.out.println();
        System.out.println(gameEnum.population.regex + tmp.getTotalPopulation());
        System.out.println(gameEnum.happiness.regex + tmp.getHappiness());
//        System.out.println(tmp.get); //TODO: resource
        System.out.println(gameEnum.food.regex + tmp.getFood());
        System.out.println(gameEnum.cup.regex + tmp.getCup());
        System.out.println(gameEnum.cupIncome.regex + tmp.incomeCup());
        System.out.println(gameEnum.gold.regex + tmp.getGold());
        System.out.println(gameEnum.goldIncome.regex + tmp.incomeGold());
        if(tmp.getUnits().size() != 0)
            System.out.println(infoCommands.units.regex);
        showAllUnits(tmp);
        System.out.println("\n\nyou can also see the other players information\npick a number: ");
        int number = 0;
        ArrayList<Player> newArr = new ArrayList<>();
        for(Player player : gameController.getPlayers())
            if(player != gameController.getPlayerTurn()) {
                System.out.println((number + 1) + ": " + player.getCivilization().name());
                newArr.add(player);
                number++;
            }
        System.out.println((number + 1) + ": score board");
        System.out.println((number + 2) + infoCommands.backToGame.regex);
        int pickNum = getNumber(scanner, number + 2);
        if(pickNum == number + 1)
            showScoreBoard(scanner, tmp);
        else if(pickNum != number + 2)
            showDemographic(newArr.get(pickNum - 1), scanner);
    }
    private static void showMilitary(Scanner scanner)
    {
        if (gameController.getPlayerTurn().getUnits().size() == 0)
            System.out.println("you have not any unit");
        else
        {
            showAllUnits(gameController.getPlayerTurn());
            System.out.println("1: " + infoCommands.backToGame.regex);
            getNumber(scanner, 1);
        }
    }
    private static void showUnits(Scanner scanner)
    {
        ArrayList<Unit> tmp = gameController.getPlayerTurn().getUnits();
        int max = gameController.getPlayerTurn().getUnits().size(), number = 0;
        if(max != 0)
            System.out.println("choose unit number to change active/inactive");
        while (number != max + 1)
        {
            if(max != 0)
                System.out.println("   unit type\t\tcoordinates\t\tunit state");
            for (int i = 0; i < max; i++)
            {
                Unit unit = gameController.getPlayerTurn().getUnits().get(i);
                System.out.print((i + 1) + ": " + unit.toString().toLowerCase(Locale.ROOT));
                printSpace(20 - unit.toString().length());
                System.out.print(unit.getTile().getPosition().X + "," + unit.getTile().getPosition().Y + "\t\t\t\t");
                System.out.println(unit.getUnitState().symbol);
            }
            System.out.println((max + 1) + ": go to Military panel");
            System.out.println((max + 2) + infoCommands.backToGame.regex);
            number = getNumber(scanner, max + 2);
            if (number == max + 1)
                showMilitary(scanner);
            else if(number == max + 2)
                number = max + 1;
            else
            {
                if(tmp.get(number - 1).getUnitState().equals(UnitState.ACTIVE))
                    tmp.get(number - 1).setUnitState(UnitState.SLEEPING);
                else
                    tmp.get(number - 1).setUnitState(UnitState.ACTIVE);
            }
        }
    }
    private static void showEconomics(Scanner scanner)
    {
        ArrayList<City> n = gameController.getPlayerTurn().getCities();
        for(City city : gameController.getPlayerTurn().getSeizedCities())
            if(city.getState() == CityState.ATTACHED)
                n.add(city);
        if(n.size() != 0)
            System.out.println("  city name  \tpopulation\tpower force\tfood yield\tcup " +
                    "yield\tgold yield\tproduction yield\tposition\tcurrent Construction\tturns\tattached");
        for (City city : n) {
            System.out.print(city.getName());
            printSpace(18 - city.getName().length());
            System.out.print(city.getCitizens().size());
            printSpace(14 - numberOfDigits(city.getCitizens().size()));
            System.out.print(city.getCombatStrength());
            printSpace(11 - numberOfDigits(city.getCombatStrength()));
            System.out.print(city.getFoodYield());
            printSpace(11 - numberOfDigits(city.getFoodYield()));
            System.out.print(city.getCupYield());
            printSpace(12 - numberOfDigits(city.getCupYield()));
            System.out.print(city.getGoldYield());
            printSpace(13 - numberOfDigits(city.getGoldYield()));
            System.out.print(city.getCitizens().size());
            printSpace(16 - numberOfDigits(city.getCitizens().size()));
            System.out.print(city.getCapitalTile().getPosition().X + "," + city.getCapitalTile().getPosition().Y);
            printSpace(13);
            System.out.print(city.getCurrentConstruction());
            printSpace(15);
            System.out.print(city.getInLineConstructionTurn());
            printSpace(3);
            if (city.getState() == CityState.ATTACHED)
                System.out.println("attached");
            else
                System.out.println("not attached");
        }
        System.out.println("1: " + infoCommands.searchCity.regex);
        System.out.println("2: " + infoCommands.backToGame.regex);
        int number = getNumber(scanner, 2);
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
                            (Technology.values()[i].cost / 10 - tmp.getResearchingTechCounter()[i]));
                    if(gameController.requiredTechForBuilding(Technology.values()[i]) != null)
                        System.out.println(infoCommands.willGain.regex + gameController.requiredTechForBuilding(Technology.values()[i]).name());
                    if(gameController.requiredTechForImprovement(Technology.values()[i]) != null)
                        System.out.println(infoCommands.willGain.regex + gameController.requiredTechForImprovement(Technology.values()[i]).name());
                }
                candidateTechs.add(Technology.values()[i]);
                max++;
            }
        System.out.println((max + 1) + infoCommands.backToGame.regex);
        int number = getNumber(scanner, max + 1);
        int flg = -1;
        if(number != max + 1)
            for(int i = 0; i < Technology.values().length; i++)
                if(Technology.values()[i] == candidateTechs.get(number - 1)) flg = i;
        if(number == flag)
            System.out.println(infoCommands.alreadyResearching.regex);
        else if(number != max + 1 && tmp.getCup() >= candidateTechs.get(number - 1).cost / 10 - tmp.getResearchingTechCounter()[flg])
        {
            tmp.setResearchingTechnology(candidateTechs.get(number - 1));
            System.out.println(infoCommands.choose.regex + candidateTechs.get(number - 1).name() + infoCommands.successful.regex);
            tmp.reduceCup();
        }
        else if(number != max + 1)
            System.out.println(infoCommands.enoughCup.regex + candidateTechs.get(number - 1).name());
    }
    private static void printCities(Player player)
    {
        int destroyedCities = 0;
        for(City city : player.getSeizedCities())
            if(city.getState() == CityState.DESTROYED)
                destroyedCities++;
        int size = player.getCities().size() + player.getSeizedCities().size() - destroyedCities;
        System.out.println(infoCommands.cities.regex);
        if(size == 0)
            System.out.println(infoCommands.nothing.regex);
        else
        {
            for (int i = 0; i < player.getCities().size(); i++)
            {
                if(player.getCities().get(i) == player.getCurrentCapitalCity())
                    System.out.println((i + 1) + ": " + player.getCities().get(i).getName() + " (capital city)");
                else
                    System.out.println((i + 1) + ": " + player.getCities().get(i).getName());
            }
            int attachedCities = 0;
            for (int i = 0; i < player.getSeizedCities().size() - destroyedCities; i++)
            {
                if(player.getSeizedCities().get(i).getState() == CityState.ATTACHED) {
                    System.out.println((attachedCities + player.getCities().size() + 1) + ": " + player.getSeizedCities().get(i).getName() + " (attached)");
                    attachedCities++;
                }
            }
        }
    }
    private static void showAllCities(Scanner scanner)
    {
        int destroyedCities = 0;
        for(City city : gameController.getPlayerTurn().getSeizedCities())
            if(city.getState() == CityState.DESTROYED)
                destroyedCities++;
        ArrayList<City> tmp = new ArrayList<>();
        for(City city : gameController.getPlayerTurn().getSeizedCities())
            if(city.getState() == CityState.ATTACHED)
                tmp.add(city);
        int max = gameController.getPlayerTurn().getCities().size() +
                gameController.getPlayerTurn().getSeizedCities().size() -
                destroyedCities;
        printCities(gameController.getPlayerTurn());
        System.out.println((max + 1) + infoCommands.searchEconomic.regex);
        System.out.println((max + 2) + infoCommands.backToGame.regex);
        int number = getNumber(scanner, max + 2);
        if(number == max + 1)
            showEconomics(scanner);
        else if(number != max + 2)
        {
            if(number <= gameController.getPlayerTurn().getCities().size())
            {
                gameController.getPlayerTurn().setSelectedCity(gameController.getPlayerTurn().getCities().get(number - 1));
                showCity();
                gameController.getPlayerTurn().setSelectedCity(null);
            }
            else
            {
                gameController.getPlayerTurn().setSelectedCity(tmp.get(number - gameController.getPlayerTurn().getCities().size() - 1));
                showCity();
            }
        }
    }
    private static void showUnit()
    {
        Unit tmp = gameController.getPlayerTurn().getSelectedUnit();

        System.out.println("owner: " + tmp.getRulerPlayer().getCivilization().name().toLowerCase(Locale.ROOT));
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
        Player player = gameController.getPlayerTurn();
        City tmp = player.getSelectedCity();
        int flg = -1;
        for(int i = 0; i < Technology.values().length; i++)
            if(Technology.values()[i] == player.getResearchingTechnology()) flg = i;
        System.out.println(infoCommands.cityName.regex + tmp.getName());
        System.out.println(gameEnum.foodYield.regex + tmp.getFoodYield());
        System.out.println(gameEnum.production.regex + tmp.getProductionYield());
        System.out.println(gameEnum.goldYield.regex + tmp.getGoldYield());
        System.out.println(gameEnum.cupYield.regex + tmp.getCupYield());
        System.out.println(infoCommands.size.regex + tmp.getTerritory().size());
        System.out.println(gameEnum.population.regex + tmp.getCitizens().size());
        System.out.println(gameEnum.power.regex + tmp.getCombatStrength());
        if(flg > -1)
        {
            System.out.println(infoCommands.currentResearching.regex + gameController.getPlayerTurn().getResearchingTechnology().name());
            System.out.println(infoCommands.remainingTurns.regex + (player.getResearchingTechnology().cost - player.getResearchingTechCounter()[flg]));
        }
        else
        {
            System.out.println(infoCommands.currentResearching.regex + infoCommands.nothing.regex);
            System.out.println(infoCommands.remainingTurns.regex + "-");
        }
        System.out.println(gameEnum.employedCitizens.regex + (tmp.employedCitizens()));
        System.out.println(gameEnum.unEmployedCitizens.regex + (gameController.getPlayerTurn().getTotalPopulation() - tmp.employedCitizens()));
        if(tmp.getCurrentConstruction() != null) {
            System.out.println(gameEnum.currentConstruction.regex + tmp.getCurrentConstruction().toString());
            System.out.println(infoCommands.remainingTurns.regex + tmp.getInLineConstructionTurn());
        }
        else
            System.out.println(gameEnum.currentConstruction.regex + infoCommands.nothing.regex);
    }
    private static void showCivilizations()
    {
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
    }
    private static void pickCivilizationAndCreatePlayers(Scanner scanner, ArrayList<User> usersToPlay) // and create players
    {
        for(User user : usersToPlay)
        {
            System.out.println(user.getUsername() + gameEnum.pickCivilization.regex);
            showCivilizations();
            outerLoop:
            while(gameController.getPlayers().size() < usersToPlay.size())
            {
                String chosenNumberStr = scanner.nextLine();
                if(!chosenNumberStr.matches("^[-+]?\\d+$"))
                {
                    System.out.println("invalid input");
                    continue;
                }
                int chosenNumber = Integer.parseInt(chosenNumberStr);
                if(chosenNumber < 1 || chosenNumber > Civilization.values().length)
                {
                    System.out.println("invalid number");
                    continue;
                }
                for(Player player : gameController.getPlayers())
                    if(player.getCivilization().equals(Civilization.values()[chosenNumber - 1]))
                    {
                        System.out.println("already taken by another player");
                        continue outerLoop;
                    }

                gameController.addPlayer(new Player(Civilization.values()[chosenNumber - 1], user.getUsername(),
                        user.getNickname(), user.getPassword(), user.getScore()));
                break;
            }
        }
        System.out.println("game started !!!!!!");
    }
    public static void runGame(Scanner scanner, HashMap<String, String> usersInfo)
    {
        Matcher matcher;
        ArrayList<User> tmpUsers = gameController.convertMapToArr(usersInfo); //Note: user[0] is loggedInUser! [loggedInUser, user1, user2, ...]
        pickCivilizationAndCreatePlayers(scanner, tmpUsers);
        gameController.initGame();
        gameController.setFirstHappiness();

        String command = null;
        do
        {
            while (gameController.getPlayers().size() > 0)
            {
                // alert some units. this method alerts all units that are in ALERT state for all players
                gameController.stayAlert();
                //update tileStates for playerTurn
                for(Player player : gameController.getPlayers())
                    player.updateTileStates();

                showBaseFields();

                command = scanner.nextLine();

                /*cheat codes*/
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null)
                    System.out.println(gameController.increaseGold(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //Almost done
                    System.out.println(gameController.increaseTurns(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null)
                    System.out.println(gameController.increaseFood(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null)
                    System.out.println(gameController.addTechnology(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseHappiness)) != null)
                    System.out.println(gameController.increaseHappiness(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.killEnemyUnit)) != null)
                    System.out.println(gameController.killEnemyUnit(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null)
                    System.out.println(gameController.moveUnit(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseHealth)) != null)
                    System.out.println(gameController.increaseHealth(matcher));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseScore)) != null)
                    System.out.println(gameController.increaseScore(matcher));
                else if (cheatCode.compareRegex(command, cheatCode.winGame) != null) {
                    gameController.removeAllPlayers();
                    System.out.println(gameController.winGame());
                    break;
                }

                /*Info*/
                else if(infoCommands.compareRegex(command, infoCommands.infoResearch) != null)
                    System.out.println(gameController.showResearch());
                else if(infoCommands.compareRegex(command, infoCommands.infoUnits) != null)
                    showUnits(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoCities) != null)
                    showAllCities(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoDemographics) != null)
                    showDemographic(gameController.getPlayerTurn(), scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoNotifications) != null)
                    showNotifications(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoMilitary) != null)
                    showMilitary(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoEconomic) != null)
                    showEconomics(scanner);
                else if(infoCommands.compareRegex(command, infoCommands.infoTechnologies) != null)
                    showTechnologies(scanner);

                /*Select*/
                else if(selectCommands.compareRegex(command, selectCommands.selectCombat) != null)
                {
                    String selectCUnitResult = gameController.selectCUnit(command);
                    if(selectCUnitResult.equals(selectCommands.selected.regex))
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
                {
                    System.out.println(gameController.moveUnit(matcher));
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.sleep) != null)
                {
                    System.out.println(gameController.sleep());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.alert) != null)
                {
                    System.out.println(gameController.alert());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.fortify) != null)
                {
                    System.out.println(gameController.fortify());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.fortifyHeal) != null)
                {
                    System.out.println(gameController.fortifyTilHeal());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.garrison) != null)
                {
                    System.out.println(gameController.garrison());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if((matcher = unitCommands.compareRegex(command, unitCommands.setup)) != null)
                {
                    System.out.println(gameController.setup(matcher));
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.pillage) != null)
                {
                    System.out.println(gameController.pillage());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if((matcher = unitCommands.compareRegex(command, unitCommands.attack)) != null)
                {
                    String tmp = gameController.attackCity(matcher);
                    if(tmp !=null)
                        System.out.println(tmp);
                    else
                    {
                        City seizedCity = gameController.belongToCity(gameController.
                                getTileByXY(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
                        int number = cityDestroy(scanner); //number 1: destroy number 2: seized
                        if(number == 1)
                            System.out.println(gameController.destroyCity(seizedCity));
                        if(number == 2)
                            System.out.println(gameController.attachCity(seizedCity));
                    }
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if((matcher = gameEnum.compareRegex(command, gameEnum.lockCitizenToTile)) != null)
                {
                    System.out.println(gameController.lockCitizenToTile(matcher));
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if((matcher = gameEnum.compareRegex(command, gameEnum.unLockCitizenToTile)) != null)
                {
                    System.out.println(gameController.unLockCitizenToTile(matcher));
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.foundCity) != null)
                {
                    System.out.println(gameController.found());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.cancelMission) != null)
                {
                    System.out.println(gameController.cancel());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.wake) != null)
                {
                    System.out.println(gameController.wake());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.delete) != null)
                {
                    System.out.println(gameController.delete());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildRoad) != null)
                    System.out.println(gameController.road());
                else if(unitCommands.compareRegex(command, unitCommands.buildRailRoad) != null)
                    System.out.println(gameController.railRoad());
                else if(unitCommands.compareRegex(command, unitCommands.buildFarm) != null)
                {
                    System.out.println(gameController.farm());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildMine) != null)
                {
                    System.out.println(gameController.mine());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildTradingPost) != null)
                {
                    System.out.println(gameController.tradingPost());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildLumbermill) != null)
                {
                    System.out.println(gameController.lumberMill());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildPasture) != null)
                {
                    System.out.println(gameController.pasture());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildCamp) != null)
                {
                    System.out.println(gameController.camp());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildPlantation) != null)
                {
                    System.out.println(gameController.plantation());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildQuarry) != null)
                {
                    System.out.println(gameController.quarry());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.buildFactory) != null)
                {
                    System.out.println(gameController.factory());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.removeJungle) != null)
                {
                    System.out.println(gameController.removeJungle());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.removeRoute) != null)
                {
                    System.out.println(gameController.removeRoute());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }
                else if(unitCommands.compareRegex(command, unitCommands.repair) != null)
                {
                    System.out.println(gameController.repair());
                    gameController.getPlayerTurn().setSelectedUnit(null);
                }

                /*map*/
                else if(mapCommands.compareRegex(command, mapCommands.mapShow) != null)
                    System.out.println(gameController.getMapString());

                /*City*/
                else if((matcher = selectCommands.compareRegex(command, selectCommands.buyTile)) != null)
                {
                    System.out.println(gameController.buyTile(matcher));
                    gameController.getPlayerTurn().setSelectedCity(null);
                }
                else if(selectCommands.compareRegex(command, selectCommands.buyUnit) != null)
                {
                    if(gameController.getPlayerTurn().getSelectedCity() != null) {
                        String type = buyUnit(scanner);
                        if(type != null)
                            System.out.println(gameController.buyUnit(type));
                        gameController.getPlayerTurn().setSelectedCity(null);
                    }
                    else
                        System.out.println(gameEnum.nonSelect.regex);
                }
                else if(gameEnum.compareRegex(command, gameEnum.buildUnit) != null)
                {
                    String tmp, buildResult;
                    if((tmp = buyUnit(scanner)) != null)
                        if((buildResult = gameController.buildUnit(tmp)) != null) {
                            System.out.println(buildResult);
                            gameController.getPlayerTurn().setSelectedCity(null);
                        }
                }
                /*others*/
                else if(gameEnum.compareRegex(command, gameEnum.end) != null)
                {
                    gameController.removeAllPlayers();
                    System.out.println(gameEnum.endGame.regex);
                    break;
                } //end game
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
            }
            gameController.handleUnitCommands();
            gameController.updatePlayersUnitLocations();
            gameController.updateWorkersConstructions();
        } while (gameController.getPlayers().size() > 0);
    }
    public static void runGameMenu()
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
                gameController.startNewGame(command, players);
                if(gameController.startNewGame(command, players).equals(gameEnum.successfulStartGame.regex))
                    runGame(scanner, players);
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