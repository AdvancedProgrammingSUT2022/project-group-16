package Controllers;

import Models.Menu.Menu;
import enums.profileEnum;
import enums.mainCommands;


import java.util.Scanner;
import java.util.regex.Matcher;

public class ProfileController
{
	private static int doesUsernameExist(String username)
	{
		for(int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if(Menu.allUsers.get(i).getUsername().equals(username))
				return i;
		}
		return -1;
	}

	private static int doesNicknameExist(String nickName)
	{
		for(int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if(Menu.allUsers.get(i).getNickname().equals(nickName))
				return i;
		}
		return -1;
	}

	private static Matcher matchRegex(Matcher matcher, String line, mainCommands regex)
	{
		if(matcher == null && (matcher = mainCommands.compareRegex(line, regex)) != null)
			matcher = mainCommands.compareRegex(line, regex);
		return matcher;
	}

	public static String changeNickname(String command)
	{
		Matcher nicknameMatcher = null;

		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i);
			nicknameMatcher = matchRegex(nicknameMatcher, sub, mainCommands.getNickname);
			nicknameMatcher = matchRegex(nicknameMatcher, sub, mainCommands.shortFormNickname);
		}
		if(nicknameMatcher != null)
		{
			String nickname = nicknameMatcher.group("nickname");

			if(doesNicknameExist(nickname) != -1)
				return (mainCommands.specificNickname.regex + nickname + mainCommands.alreadyExist.regex);
			else
			{
				Menu.loggedInUser.setNickname(nickname);
				Menu.allUsers.get(doesUsernameExist(Menu.loggedInUser.getUsername())).setNickname(nickname);
				RegisterController.writeDataOnJson();
				return profileEnum.successfulNicknameChange.regex;
			}
		}
		else
			return mainCommands.invalidCommand.regex;
	}

	public static String changePassword(Matcher oldPassword, Matcher newPassword, String command)
	{
		oldPassword = null;
		newPassword = null;

		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i, command.length());
			if(newPassword == null && (newPassword = mainCommands.compareRegex(sub, mainCommands.getNewPassword)) != null)
				newPassword = mainCommands.compareRegex(sub, mainCommands.getNewPassword);
			else if(oldPassword == null && (oldPassword = mainCommands.compareRegex(sub, mainCommands.getOldPassword)) != null)
				oldPassword = mainCommands.compareRegex(sub, mainCommands.getOldPassword);
		}
		if (newPassword != null && oldPassword != null)
		{
			String currPass = oldPassword.group("password");
			String newPass = newPassword.group("password");

			if(!Menu.loggedInUser.getPassword().equals(currPass))
				return profileEnum.invalidOldPass.regex;
			else if(currPass.equals(newPass))
				return profileEnum.commonPasswords.regex;
			else if(RegisterController.checkWeaknessOfPassword(newPass) != null)
				return mainCommands.weakNewPass.regex;
			else
			{
				Menu.loggedInUser.setPassword(newPass);
				Menu.allUsers.get(doesUsernameExist(Menu.loggedInUser.getUsername())).setPassword(newPass);
				RegisterController.writeDataOnJson();
				return profileEnum.successfulPassChange.regex;
			}
		}
		else
			return mainCommands.invalidCommand.regex;
	}

	public static String enterMenu(Scanner scanner, Matcher matcher)
	{
		String menuName = matcher.group("menuName");

		if((matcher = mainCommands.compareRegex(menuName, mainCommands.startNewGame)) != null)
			return mainCommands.navigationError.regex;
		else if((matcher = mainCommands.compareRegex(menuName, mainCommands.loginMenu)) != null)
			return mainCommands.navigationError.regex;
		else if((matcher = mainCommands.compareRegex(menuName, mainCommands.mainMenu)) != null)
			return "1";
		return mainCommands.invalidCommand.regex;
	}
}
