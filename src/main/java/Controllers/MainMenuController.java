package Controllers;

import Models.Menu.Menu;
import Views.gameMenuView;
import Views.profileMenuVeiw;
import enums.mainCommands;
import enums.mainMenuEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenuController
{
	public static void startNewGame()
	{
		gameMenuView.run();
	}

	public static void enterProfileMenu(Scanner scanner, Matcher matcher)
	{
		profileMenuVeiw.run(scanner, matcher);
	}

	public static String enterMenu(Scanner scanner, Matcher matcher)
	{
		String menuName = matcher.group("menuName");
		if((matcher = mainCommands.compareRegex(menuName, mainCommands.profileName)) != null)
		{
			enterProfileMenu(scanner, matcher);
			return "1";
		}
		else if((matcher = mainCommands.compareRegex(menuName, mainCommands.startNewGame)) != null)
		{
			startNewGame();
			return "1";
		}
		else if((matcher = mainCommands.compareRegex(menuName, mainCommands.loginMenu)) != null)
			return mainCommands.navigationError.regex;
		else
			return mainCommands.invalidCommand.regex;
	}

	public static String logoutUser()
	{
		Menu.loggedInUser = null;
		return mainMenuEnum.successfulLogout.regex;
	}
}
