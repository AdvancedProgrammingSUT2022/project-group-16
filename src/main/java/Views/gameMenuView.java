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
    public static Tile selectedTile;
    public static Unit selectedUnit = null;

    public static void startGame(Scanner scanner, HashMap<String,String> Map)
    {
        GameController gameController = GameController.getInstance();
        Matcher matcher;
        Player playerTurn;

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
            int number = 0;
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

            players.add(new Player(GameController.findCivilByNumber(number), tmpUsers[i].getUsername(),
                    tmpUsers[i].getNickname(), tmpUsers[i].getPassword()));
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
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoResearch)) != null)
                    System.out.println(GameController.showResearch(playerTurn)); //done
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoUnits)) != null)
                    GameController.showUnits(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoCities)) != null)
                    GameController.showCities(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDiplomacy)) != null)
                    GameController.showDiplomacy(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoVictory)) != null)
                    GameController.showVictory(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDemographics)) != null)
                    GameController.showDemographics(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoNotifications)) != null)
                    GameController.showNotifications(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoMilitary)) != null)
                    GameController.showMilitary(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoEconomic)) != null)
                    GameController.showEconomic(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDiplomatic)) != null)
                    GameController.showDiplomatic(playerTurn); //TODO
                else if((matcher = infoCommands.compareRegex(command, infoCommands.infoDeals)) != null)
                    GameController.showDeals(playerTurn); //TODO

                /*Select*/
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectCombat)) != null)
                    GameController.selectUnitCombat(); //TODO
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectNonCombat)) != null)
                    GameController.selectUnitNonCombat(); //TODO
                else if((matcher = selectCommands.compareRegex(command, selectCommands.selectCity)) != null)
                    GameController.selectCity(); //TODO

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
                else if(command.equals("end")) break;
                else
                    System.out.println(mainCommands.invalidCommand.regex);
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