package Models;

import java.net.URL;

public class User
{
	private final String username;
	private String nickname;
	private String password;
	private URL photo;
	private int score;
	
	public User(String username, String nickname, String password, URL photo)
	{
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.photo = photo;
		this.score = 0;
	}
	
	public String getUsername()
	{
		return username;
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
	public URL getPhoto() {
		return photo;
	}
	public void setPhoto(URL photo) {
		this.photo = photo;
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
