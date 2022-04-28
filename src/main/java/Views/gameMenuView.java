package Views;

import Controllers.GameController;
import Controllers.MainMenuController;
import Controllers.RegisterController;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.User;
import enums.cheatCode;
import enums.gameEnum;
import enums.mainCommands;
import enums.mainMenuEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;

public class gameMenuView
{
    public static void startGame(Scanner scanner, HashMap<String,String> Map)
    {
        GameController gameController = GameController.getInstance();
        Matcher matcher;
        Player turn;

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
            turn = players.get(num);

            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();

                //cheat codes
                if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null) //done
                    System.out.println(GameController.increaseGold(matcher, turn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //TODO
                    System.out.println(GameController.increaseTurns(matcher, turn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null) //done
                    System.out.println(GameController.increaseFood(matcher, turn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null) //done
                    System.out.println(GameController.addTechnology(matcher, turn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.winBattle)) != null) //TODO
                    System.out.println(GameController.winBattle(matcher, turn));
                else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null) //TODO
                    System.out.println(GameController.moveUnit(matcher, turn));
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showResearch)) != null) //done
                    System.out.println(GameController.showResearch(turn));
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showUnits)) != null) //done
                {
                    if(turn.getUnits().size() == 0) System.out.println(mainCommands.nothing.regex);
                    else
                    {
                        for(int i = 0; i < turn.getUnits().size(); i++)
                            System.out.println(i + ": " + turn.getUnits().get(i).getTile().getPosition().X + ", " + turn.getUnits().get(i).getTile().getPosition().Y);
                    }
                }
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showCities)) != null) //done
                    if(turn.getCities().size() == 0) System.out.println(mainCommands.nothing.regex);
                    else
                    {
                        for(int i = 0; i < turn.getCities().size(); i++)
                            System.out.println(i + ": " + turn.getCities().get(i).getCapitalTile().getPosition().X +", " + turn.getCities().get(i).getCapitalTile().getPosition().Y);
                    }
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showDiplomacy)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showVictory)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showDEMOGRAPHICS)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showNOTIFICATIONS)) != null) //done
                    if(turn.getNotifications().size() == 0) System.out.println(mainCommands.nothing.regex);
                    else
                    {
                        for(int i = 0; i < turn.getNotifications().size(); i++)
                            System.out.println(i + ": " + turn.getNotifications().get(i).getMessage());
                    }
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showMILITARY)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showECONOMIC)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showDIPLOMATIC)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
                else if ((matcher = gameEnum.compareRegex(command, gameEnum.showDEALS)) != null) //TODO
                    System.out.println(turn.getResearchingTechnology());
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