package Controllers;

import Models.Menu.Menu;
import Views.gameMenuView;
import Views.profileMenuVeiw;
import enums.mainCommands;
import enums.mainMenuEnum;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenuController
{
	public String enterMenu(Scanner scanner, Matcher matcher) throws IOException {
		String menuName = matcher.group("menuName");
		if(mainCommands.compareRegex(menuName, mainCommands.profileName) != null)
		{
			profileMenuVeiw.run(scanner);
			return "1";
		}
		else if(mainCommands.compareRegex(menuName, mainCommands.startNewGame) != null)
		{
			gameMenuView.runGameMenu();
			return "1";
		}
		else if(mainCommands.compareRegex(menuName, mainCommands.loginMenu) != null)
			return mainCommands.navigationError.regex;
		else
			return mainCommands.invalidCommand.regex;
	}

	public String logoutUser()
	{
		Menu.loggedInUser = null;
		return mainMenuEnum.successfulLogout.regex;
	}
}
