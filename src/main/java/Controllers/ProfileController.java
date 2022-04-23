package Controllers;

import Models.Menu.Menu;

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
			Menu.loggedInUser.changeNickname(nickName);
			return "nickname changed successfully!";
		}
	}
	
	private void changePassword(String password)
	{
	
	}
	
	private boolean isPasswordCorrect(String password)
	{
		return false;
	}
	
}
