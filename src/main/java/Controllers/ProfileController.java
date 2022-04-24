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

	public static String changePassword(Matcher matcher)
	{
		String currPass = matcher.group("currentPassword");
		String newPass = matcher.group("newPassword");
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
}
