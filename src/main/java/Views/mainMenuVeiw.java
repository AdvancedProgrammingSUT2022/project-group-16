package Views;

import Controllers.MainMenuController;
import enums.mainCommands;
import enums.mainMenuEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class mainMenuVeiw
{

    public static void run(Scanner scanner, Matcher matcher) {
        String command;

        while(scanner.hasNextLine()) {
            command = scanner.nextLine().trim();

            if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.logoutUser)) != null) {
                System.out.println(MainMenuController.logoutUser());
                break;
            }
            else if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.showCurrentMenu)) != null)
                System.out.println(mainMenuEnum.currentMenu.regex);
            else if((matcher = mainMenuEnum.compareRegex(command, mainMenuEnum.menuExit)) != null)
                break;
            else if((matcher = mainCommands.compareRegex(command, mainCommands.enterMenu)) != null)
            {
                if(!MainMenuController.enterMenu(scanner, matcher).equals("1"))
                    System.out.println(MainMenuController.enterMenu(scanner, matcher));
            }
            else
                System.out.println(mainMenuEnum.invalidCommand.regex);
        }
    }
}
