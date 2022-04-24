package Controllers;

import Models.Menu.Menu;
import enums.profileEnum;
import enums.mainCommands;


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

	public static String changeNickname(String nickName)
	{
		if(doesNicknameExist(nickName) != -1)
			return (mainCommands.specificNickname.regex + nickName + mainCommands.alreadyExist.regex);
		else
		{
			Menu.loggedInUser.setNickname(nickName);
			Menu.allUsers.get(doesUsernameExist(Menu.loggedInUser.getUsername())).setNickname(nickName);
			RegisterController.writeDataOnJson();
			return profileEnum.successfulNicknameChange.regex;
		}
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
}
