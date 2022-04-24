package Views;

import Controllers.GameController;
import enums.gameEnum;
import java.util.Scanner;
import java.util.regex.Matcher;

public class gameMenuView
{
    public static void startGame()
    {

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
                System.out.println(GameController.startNewGame(command));
                if(GameController.startNewGame(command).equals("game started"))
                    startGame();//game started
            }
            else if((matcher = gameEnum.compareRegex(command, gameEnum.menuExit)) != null)
                break;
            else if((matcher = gameEnum.compareRegex(command, gameEnum.showCurrentMenu)) != null)
                System.out.println("Game Menu");
            else
                System.out.println("invalid command");

        }
    }

}