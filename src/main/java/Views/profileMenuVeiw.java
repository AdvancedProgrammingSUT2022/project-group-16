package Views;

import Controllers.MainMenuController;
import Controllers.ProfileController;
import enums.mainCommands;
import enums.mainMenuEnum;
import enums.profileEnum;
import enums.registerEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class profileMenuVeiw
{
    public static void run(Scanner scanner, Matcher matcher)
    {
        String command;

        while(true)
        {
            command = scanner.nextLine();

            if((matcher = profileEnum.compareRegex(command, profileEnum.changeNickname)) != null)
                System.out.println(ProfileController.changeNickname(matcher.group("newNickname")));
            else if((matcher = profileEnum.compareRegex(command, profileEnum.changePassword)) != null)
                System.out.println(ProfileController.changePassword(matcher));
            else if((matcher = mainCommands.compareRegex(command, mainCommands.showCurrentMenu)) != null)
                System.out.println(profileEnum.currentMenu.regex);
            else if((matcher = mainCommands.compareRegex(command, mainCommands.menuExit)) != null)
                break;
            else
                System.out.println(mainCommands.invalidCommand.regex);
        }
    }
}