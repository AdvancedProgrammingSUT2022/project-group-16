package Views;

import Controllers.GameController;
import Controllers.RegisterController;
import Models.User;
import enums.cheatCode;
import enums.gameEnum;
import enums.mainCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;

public class gameMenuView
{
    public static void startGame(Scanner scanner, HashMap<String,String> players)
    {
        GameController gameController = GameController.getInstance();
        String command;
        Matcher matcher;

        while (scanner.hasNextLine())
        {
            command = scanner.nextLine();

            //cheat codes
            if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null)
                System.out.println(GameController.increaseGold(matcher));
            else if ((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null)
                System.out.println(GameController.increaseTurns(matcher));
            else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null)
                System.out.println(GameController.increaseFood(matcher));
            else if ((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null)
                System.out.println(GameController.addTechnology(matcher));
            else if ((matcher = cheatCode.compareRegex(command, cheatCode.winBattle)) != null)
                System.out.println(GameController.winBattle(matcher));
            else if ((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null)
                System.out.println(GameController.moveUnit(matcher));
            else
                System.out.println(mainCommands.invalidCommand.regex);
        }
        //TODO:start new game with players and loggedIn player
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
            else if((matcher = mainCommands.compareRegex(command, mainCommands.showCurrentMenu)) != null)
                System.out.println(gameEnum.currentMenu.regex);
            else
                System.out.println(mainCommands.invalidCommand.regex);

        }
    }
}