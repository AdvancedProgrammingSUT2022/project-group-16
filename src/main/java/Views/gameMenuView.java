package Views;

import Controllers.GameController;
import Models.Game.Position;
import Models.Player.Player;
import Models.Terrain.Tile;
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
    public static void startGame(Scanner scanner, HashMap<String,String> Map)
    {
        GameController gameController = GameController.getInstance();
        Matcher matcher;
        Player playerTurn;
        Tile selectedTile;
        Unit selectedUnit = null;

        User[] tmpUsers = GameController.convertMapToArr(Map); //Note: player[0] is loggedInUSer! [loggedInUser, player1, player2, ...]

        ArrayList<Player> players = new ArrayList<>();

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
                number = scanner.nextInt();
                System.out.println(GameController.pickCivilization(players, tmpUsers, scanner, gameController, number, i));
            } while (number > 10 || number < 1 || GameController.inArr(players, GameController.findCivilByNumber(number)));
            players.add(new Player(GameController.findCivilByNumber(number), tmpUsers[i].getUsername(),
                    tmpUsers[i].getNickname(), tmpUsers[i].getPassword(), gameController));
        }

        int num = 0;

        while (true)
        {
            playerTurn = players.get(num);
            System.out.println(playerTurn.getUsername() + gameEnum.turn.regex);

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();

                /*cheat codes*/
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null) //done
                    System.out.println(GameController.increaseGold(matcher, playerTurn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //TODO
                    System.out.println(GameController.increaseTurns(matcher, playerTurn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null) //done
                    System.out.println(GameController.increaseFood(matcher, playerTurn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null) //done
                    System.out.println(GameController.addTechnology(matcher, playerTurn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.winBattle)) != null) //TODO
                    System.out.println(GameController.winBattle(matcher, playerTurn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null) //TODO
                    System.out.println(GameController.moveUnit(matcher, playerTurn));

                /*Info*/
                else if((matcher = gameEnum.compareRegex(command, gameEnum.info)) != null)
                {
                    System.out.println(infoCommands.showResearch.regex);
                    System.out.println(infoCommands.showUnits.regex);
                    System.out.println(infoCommands.showCities.regex);
                    System.out.println(infoCommands.showDiplomacy.regex);
                    System.out.println(infoCommands.showVictory.regex);
                    System.out.println(infoCommands.showDEMOGRAPHICS.regex);
                    System.out.println(infoCommands.showNOTIFICATIONS.regex);
                    System.out.println(infoCommands.showMILITARY.regex);
                    System.out.println(infoCommands.showECONOMIC.regex);
                    System.out.println(infoCommands.showDIPLOMATIC.regex);
                    System.out.println(infoCommands.showDEALS.regex);

                    int number = 0;
                    while (number == 0)
                    {
                        number = GameController.getNum(scanner, 1, 11);
                        if(number == 0)
                            System.out.println(mainCommands.pickBetween.regex + "1 and 11");
                    }

                    switch (number % 11)
                    {
                        case 1 -> System.out.println(GameController.showResearch(playerTurn)); //done (show research)
                        case 2 ->  //done (show units)
                        {
                            if (playerTurn.getUnits().size() == 0) System.out.println(mainCommands.nothing.regex);
                            else {
                                for (int i = 0; i < playerTurn.getUnits().size(); i++)
                                    System.out.println(i + ": " + playerTurn.getUnits().get(i).getTile().getPosition().X + ", " + playerTurn.getUnits().get(i).getTile().getPosition().Y);
                            }
                        }
                        case 3 -> //done (show cities)
                        {
                            if (playerTurn.getCities().size() == 0) System.out.println(mainCommands.nothing.regex);
                            else {
                                for (int i = 0; i < playerTurn.getCities().size(); i++)
                                    System.out.println(i + ": " + playerTurn.getCities().get(i).getCapitalTile().getPosition().X + ", " + playerTurn.getCities().get(i).getCapitalTile().getPosition().Y);
                            }
                        }
                        case 4 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                        case 5 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                        case 6 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                        case 7 -> //done(show notifications)
                        {
                            if (playerTurn.getNotifications().size() == 0)
                                System.out.println(mainCommands.nothing.regex);
                            else {
                                for (int i = 0; i < playerTurn.getNotifications().size(); i++)
                                    System.out.println(i + ": " + playerTurn.getNotifications().get(i).getMessage());
                            }
                        }
                        case 8 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                        case 9 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                        case 10 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                        case 0 -> System.out.println(playerTurn.getResearchingTechnology()); //TODO
                    }
                }

                /*Select*/
                else if((matcher = gameEnum.compareRegex(command, gameEnum.select)) != null)
                {
                    System.out.println(selectCommands.unit.regex);
                    System.out.println(selectCommands.city.regex);

                    int number = 0;
                    while (number == 0)
                    {
                        number = GameController.getNum(scanner,1, 2);
                        if(number == 0)
                            System.out.println(mainCommands.pickBetween.regex + "1 and 2");
                    }

                    switch (number % 2)
                    {
                        case 1 ->
                                {
                                    System.out.println(selectCommands.combat.regex);
                                    System.out.println(selectCommands.nonCombat.regex);

                                    int tmp = 0;
                                    while (tmp == 0)
                                    {
                                        tmp = GameController.getNum(scanner,1, 2);
                                        if(tmp == 0)
                                            System.out.println(mainCommands.pickBetween.regex + "1 and 2");
                                    }

                                    System.out.println(unitCommands.position.regex);
                                    System.out.println(unitCommands.x.regex);
                                    int x = scanner.nextInt();
                                    System.out.println(unitCommands.y.regex);
                                    int y = scanner.nextInt();

                                    switch (tmp % 2)
                                    {
                                        case 1 -> GameController.selectUnitCombat(new Position(x, y));
                                        case 0 -> GameController.selectUnitNonCombat(new Position(x, y));
                                    }
                                }
                        case 0 ->
                                {
                                    System.out.println(selectCommands.name.regex);
                                    System.out.println(selectCommands.position.regex);

                                    int tmp = 0;
                                    while (tmp == 0)
                                    {
                                        tmp = GameController.getNum(scanner,1, 2);
                                        if(tmp == 0)
                                            System.out.println(mainCommands.pickBetween.regex + "1 and 2");
                                    }

                                    switch (tmp % 2)
                                    {
                                        case 1 ->
                                                {
                                                    System.out.println(selectCommands.cityName.regex);
                                                    String name = scanner.nextLine();
                                                    GameController.selectCityName(name);
                                                }
                                        case 0 ->
                                                {
                                                    System.out.println(unitCommands.position.regex);
                                                    System.out.println(unitCommands.x.regex);
                                                    int x = scanner.nextInt();
                                                    System.out.println(unitCommands.y.regex);
                                                    int y = scanner.nextInt();
                                                    GameController.selectCityPosition(new Position(x, y));
                                                }
                                    }
                                }
                    }
                }

                /*unit*/
                else if((matcher = gameEnum.compareRegex(command, gameEnum.unit)) != null)
                {
                    System.out.println(unitCommands.moveTo.regex);
                    System.out.println(unitCommands.sleep.regex);
                    System.out.println(unitCommands.alert.regex);
                    System.out.println(unitCommands.fortify.regex);
                    System.out.println(unitCommands.fortifyUntilHeal.regex);
                    System.out.println(unitCommands.garrison.regex);
                    System.out.println(unitCommands.setup.regex);
                    System.out.println(unitCommands.attack.regex);
                    System.out.println(unitCommands.found.regex);
                    System.out.println(unitCommands.cancle.regex);
                    System.out.println(unitCommands.wake.regex);
                    System.out.println(unitCommands.delete.regex);
                    System.out.println(unitCommands.build.regex);
                    System.out.println(unitCommands.remove.regex);
                    System.out.println(unitCommands.repair.regex);

                    int number = 0;
                    while (number == 0)
                    {
                        number = GameController.getNum(scanner,1, 15);
                        if(number == 0)
                            System.out.println(mainCommands.pickBetween.regex + "1 and 15");
                    }

                    switch(number % 15)
                    {
                        case 1 ->
                                {
                                    System.out.println(unitCommands.position.regex);
                                    System.out.println(unitCommands.x.regex);
                                    int x = scanner.nextInt();
                                    System.out.println(unitCommands.y.regex);
                                    int y = scanner.nextInt();
                                    GameController.moveUnit(new Position(x, y));
                                }
                        case 2 -> GameController.sleep(playerTurn, selectedUnit);
                        case 3 -> GameController.alert();
                        case 4 -> GameController.fortify();
                        case 5 -> GameController.fortifyTilHeal();
                        case 6 -> GameController.garrison();
                        case 7 -> GameController.setup();
                        case 8 ->
                                {
                                    System.out.println(unitCommands.position.regex);
                                    System.out.println(unitCommands.x.regex);
                                    int x = scanner.nextInt();
                                    System.out.println(unitCommands.y.regex);
                                    int y = scanner.nextInt();
                                    GameController.attack(new Position(x, y));
                                }
                        case 9 -> GameController.found();
                        case 10 -> GameController.cancel();
                        case 11 -> GameController.wake();
                        case 12 -> GameController.delete();
                        case 13 ->
                                {
                                    System.out.println(unitCommands.road.regex);
                                    System.out.println(unitCommands.railRoad.regex);
                                    System.out.println(unitCommands.farm.regex);
                                    System.out.println(unitCommands.mine.regex);
                                    System.out.println(unitCommands.tradingPost.regex);
                                    System.out.println(unitCommands.lumberMill.regex);
                                    System.out.println(unitCommands.pasture.regex);
                                    System.out.println(unitCommands.camp.regex);
                                    System.out.println(unitCommands.plantation.regex);
                                    System.out.println(unitCommands.quarry.regex);

                                    int tmp = 0;
                                    while (tmp == 0)
                                    {
                                        tmp = GameController.getNum(scanner,1, 10);
                                        if(tmp == 0)
                                            System.out.println(mainCommands.pickBetween.regex + "1 and 10");
                                    }

                                    switch (tmp % 10)
                                    {
                                        case 1 -> GameController.road();
                                        case 2 -> GameController.railRoad();
                                        case 3 -> GameController.farm();
                                        case 4 -> GameController.mine();
                                        case 5 -> GameController.tradingPost();
                                        case 6 -> GameController.lumberMill();
                                        case 7 -> GameController.pasture();
                                        case 8 -> GameController.camp();
                                        case 9 -> GameController.plantation();
                                        case 0 -> GameController.quarry();
                                    }
                                }
                        case 14 ->
                                {
                                    System.out.println(unitCommands.jungle.regex);
                                    System.out.println(unitCommands.route.regex);

                                    int tmp = 0;
                                    while (tmp == 0)
                                    {
                                        tmp = GameController.getNum(scanner,1, 2);
                                        if(tmp == 0)
                                            System.out.println(mainCommands.pickBetween.regex + "1 and 2");
                                    }

                                    switch (tmp % 2)
                                    {
                                        case 1 -> GameController.removeJungle();
                                        case 0 -> GameController.removeRoute();
                                    }
                                }
                        case 0 -> GameController.repair();
                    }

                }

                /*map*/
                else if((matcher = gameEnum.compareRegex(command, gameEnum.map)) != null)
                {
                    System.out.println(mapCommands.show.regex);
                    System.out.println(mapCommands.move.regex);

                    int number = 0;
                    while (number == 0)
                    {
                        number = GameController.getNum(scanner,1, 2);
                        if(number == 0)
                            System.out.println(mainCommands.pickBetween.regex + "1 and 2");
                    }

                    switch (number % 2)
                    {
                        case 1 ->
                                {
                                    System.out.println(mapCommands.byPosition.regex);
                                    System.out.println(mapCommands.byCityName.regex);

                                    int tmp = 0;
                                    while (tmp == 0)
                                    {
                                        tmp = GameController.getNum(scanner,1, 2);
                                        if(tmp == 0)
                                            System.out.println(mainCommands.pickBetween.regex + "1 and 2");
                                    }

                                    switch (tmp % 2)
                                    {
                                        case 1 ->
                                                {
                                                    System.out.println(unitCommands.position.regex);
                                                    System.out.println(unitCommands.x.regex);
                                                    int x = scanner.nextInt();
                                                    System.out.println(unitCommands.y.regex);
                                                    int y = scanner.nextInt();
                                                    GameController.mapShowPosition(new Position(x, y));
                                                }
                                        case 0 ->
                                                {
                                                    System.out.println(selectCommands.cityName.regex);
                                                    String name = scanner.nextLine();
                                                    GameController.mapShowCityName(name);
                                                }
                                    }
                                }
                        case 0 ->
                                {
                                    System.out.println(mapCommands.Right.regex);
                                    System.out.println(mapCommands.Left.regex);
                                    System.out.println(mapCommands.Up.regex);
                                    System.out.println(mapCommands.Down.regex);

                                    int tmp = 0;
                                    while (tmp == 0)
                                    {
                                        tmp = GameController.getNum(scanner,1, 4);
                                        if(tmp == 0)
                                            System.out.println(mainCommands.pickBetween.regex + "1 and 4");
                                    }

                                    System.out.println(mapCommands.numberOfMoves.regex);
                                    int moves = scanner.nextInt();

                                    switch (tmp % 4)
                                    {
                                        case 1 -> GameController.mapMoveRight(moves);
                                        case 2 -> GameController.mapMoveLeft(moves);
                                        case 3 -> GameController.mapMoveUp(moves);
                                        case 0 -> GameController.mapMoveDown(moves);
                                    }
                                }
                    }
                }
                else if(command.equals("end")) break;
            }

            //next turn
            num = (num + 1) % players.size();
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
                    gameMenuView.startGame(scanner, players);
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