package Views;

import Controllers.MainMenuController;
import enums.mainCommands;
import enums.mainMenuEnum;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class mainMenuVeiw
{
    public static void run(Scanner scanner) throws IOException {
        MainMenuController mainMenuController = new MainMenuController();
        String command;
        Matcher matcher;

        while(scanner.hasNextLine()) {
            command = scanner.nextLine().trim();

            if(mainMenuEnum.compareRegex(command, mainMenuEnum.logoutUser) != null) {
                System.out.println(mainMenuController.logoutUser());
                break;
            }
            else if(mainMenuEnum.compareRegex(command, mainMenuEnum.showCurrentMenu) != null)
                System.out.println(mainMenuEnum.currentMenu.regex);
            else if(mainMenuEnum.compareRegex(command, mainMenuEnum.menuExit) != null)
                break;
            else if((matcher = mainCommands.compareRegex(command, mainCommands.enterMenu)) != null)
            {
                if(!mainMenuController.enterMenu(scanner, matcher).equals("1"))
                    System.out.println(mainMenuController.enterMenu(scanner, matcher));
            }
            else
                System.out.println(mainMenuEnum.invalidCommand.regex);
        }
    }
}
