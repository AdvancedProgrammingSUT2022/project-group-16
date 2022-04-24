package Models;

public class User
{
	String username;
	String nickname;
	String password;
	int score;
	
	public User(String username, String nickname, String password)
	{
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.score = 0;
	}
	
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getNickname()
	{
		return nickname;
	}
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public int getScore()
	{
		return score;
	}
	public void setScore(int score)
	{
		this.score = score;
	}

}
