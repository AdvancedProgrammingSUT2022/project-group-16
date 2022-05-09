package Controllers;

import Models.Menu.Menu;
import enums.profileEnum;
import enums.mainCommands;

import java.util.regex.Matcher;

public class ProfileController
{
	RegisterController registerController = new RegisterController();
	public int doesUsernameExist(String username)
	{
		for(int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if(Menu.allUsers.get(i).getUsername().equals(username))
				return i;
		}
		return -1;
	}

	private int doesNicknameExist(String nickName)
	{
		for(int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if(Menu.allUsers.get(i).getNickname().equals(nickName))
				return i;
		}
		return -1;
	}
	public String changeNickname(Matcher matcher)
	{
		String nickname = matcher.group("newName");

		if(doesNicknameExist(nickname) != -1)
			return (mainCommands.specificNickname.regex + nickname + mainCommands.alreadyExist.regex);
		else
		{
			Menu.loggedInUser.setNickname(nickname);
			Menu.allUsers.get(doesUsernameExist(Menu.loggedInUser.getUsername())).setNickname(nickname);
			registerController.writeDataOnJson();
			return profileEnum.successfulNicknameChange.regex;
		}
	}

	public String changePassword(String command)
	{
		Matcher newPassword = null;
		Matcher oldPassword = null;
		for(int i = 0; i < command.length(); i++)
			if(newPassword == null && (newPassword = profileEnum.compareRegex(command.substring(i), profileEnum.getNewPassword)) != null)
				newPassword = profileEnum.compareRegex(command.substring(i), profileEnum.getNewPassword);
		for(int i = 0; i < command.length(); i++)
			if(oldPassword == null && (oldPassword = profileEnum.compareRegex(command.substring(i), profileEnum.getOldPassword)) != null)
				oldPassword = profileEnum.compareRegex(command.substring(i), profileEnum.getOldPassword);
		if (newPassword != null && oldPassword != null)
		{
			String currPass = oldPassword.group("password");
			String newPass = newPassword.group("password");

			if(!Menu.loggedInUser.getPassword().equals(currPass))
				return profileEnum.invalidOldPass.regex;
			else if(currPass.equals(newPass))
				return profileEnum.commonPasswords.regex;
			else if(registerController.checkWeaknessOfPassword(newPass) != null)
				return mainCommands.weakNewPass.regex;
			else
			{
				Menu.loggedInUser.setPassword(newPass);
				Menu.allUsers.get(doesUsernameExist(Menu.loggedInUser.getUsername())).setPassword(newPass);
				registerController.writeDataOnJson();
				return profileEnum.successfulPassChange.regex;
			}
		}
		else
			return mainCommands.invalidCommand.regex;
	}

	public String enterMenu(Matcher matcher)
	{
		String menuName = matcher.group("menuName");

		if(mainCommands.compareRegex(menuName, mainCommands.startNewGame) != null)
			return mainCommands.navigationError.regex;
		else if(mainCommands.compareRegex(menuName, mainCommands.loginMenu) != null)
			return mainCommands.navigationError.regex;
		else if(mainCommands.compareRegex(menuName, mainCommands.mainMenu) != null)
			return "1";
		return mainCommands.invalidCommand.regex;
	}
}
