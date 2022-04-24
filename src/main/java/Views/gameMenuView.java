package Views;

import Controllers.GameController;
import enums.gameEnum;
import enums.mainCommands;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;

public class gameMenuView
{
    public static void startGame(HashMap<String,String> players)
    {
        //TODO: start and pick Civilizations
    }

    public static void run()
    {
        String command;
        Scanner scanner = new Scanner(System.in);
        Matcher matcher;

        while(true)
        {
            command = scanner.nextLine();
            if((matcher = gameEnum.compareRegex(command, gameEnum.startGame)) != null)
            {
                HashMap<String, String> players = new HashMap<String, String>();
                System.out.println(GameController.startNewGame(command, players));
                if(GameController.startNewGame(command, players).equals(gameEnum.successfulStartGame.regex))
                    startGame(players);//game started
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