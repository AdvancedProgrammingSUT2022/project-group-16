package Controllers;

import Models.Menu.Menu;

import java.util.regex.Matcher;

public class ProfileController
{
	private boolean doesUsernameExist(String username)
	{
		for(int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if(Menu.allUsers.get(i).getUsername().equals(username))
			{
				return true;
			}
		}
		return false;
	}
	
	public static int doesNicknameExist(String nickName)
	{
		for(int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if(Menu.allUsers.get(i).getNickname().equals(nickName))
			{
				return i;
			}
		}
		return -1;
	}
	
	public static String changeNickname(String nickName)
	{
		if(doesNicknameExist(nickName) != -1)
		{
			return "user with nickname " + nickName + " already exists";
		}
		else
		{
			Menu.loggedInUser.setNickname(nickName);
			return "nickname changed successfully!";
		}
	}
	
	public static String changePassword(Matcher matcher)
	{
		if(!Menu.loggedInUser.getPassword().equals(matcher.group("currentPassword")))
		{
			return "current password is invalid";
		}
		else if(matcher.group("currentPassword").equals(matcher.group("newPassword")))
		{
			return "please enter a new password";
		}
		else
		{
			Menu.loggedInUser.setPassword(matcher.group("newPassword"));
			return "password changed successfully!";
		}
	}
	
	private boolean isPasswordCorrect(String password)
	{
		return false;
	}
	
}
