package Views;

import Controllers.MainMenuController;
import enums.mainMenuEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class mainMenuVeiw
{

    public static void run(Scanner scanner, Matcher matcher) {
        String command;

        while(true) {
            command = scanner.nextLine().trim();

            if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.logoutUser)) != null) {
                System.out.println("user logged out successfully!");
                break;
            }
            else if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.showCurrentMenu)) != null)
            {
                System.out.println("Main Menu");
            }
            else if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.menuExit)) != null)
                break;
            else if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.enterMenu)) != null)
            {
                if(MainMenuController.enterMenu(scanner, matcher) == 1)
                {
                    System.out.println("invalid command");
                }
            }
            else
            {
                System.out.println("invalid command");
            }
        }
    }
}
