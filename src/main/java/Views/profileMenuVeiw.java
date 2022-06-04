package Views;

import Controllers.ProfileController;
import enums.mainCommands;
import enums.profileEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class profileMenuVeiw
{
    public static void run(Scanner scanner)
    {
        String command;
        Matcher matcher;
        ProfileController profileController = new ProfileController();
        while(scanner.hasNextLine())
        {
            command = scanner.nextLine();

            if((matcher = profileEnum.compareRegex(command, profileEnum.changeNickname)) != null)
                System.out.println(profileController.changeNickname(matcher.group("nickname")));
            else if((matcher = profileEnum.compareRegex(command, profileEnum.shortChangeNickname)) != null)
                System.out.println(profileController.changeNickname(matcher.group("nickname")));
            else if(profileEnum.compareRegex(command, profileEnum.changePassword) != null)
                    System.out.println(profileController.changePassword(command));
            else if(mainCommands.compareRegex(command, mainCommands.showCurrentMenu) != null)
                System.out.println(profileEnum.currentMenu.regex);
            else if((matcher = mainCommands.compareRegex(command, mainCommands.enterMenu)) != null)
            {
                if(profileController.enterMenu(matcher).equals("1"))
                    break;
                else
                    System.out.println(profileController.enterMenu(matcher));
            }
            else if(mainCommands.compareRegex(command, mainCommands.menuExit) != null)
                break;
            else
                System.out.println(mainCommands.invalidCommand.regex);
        }
    }
}