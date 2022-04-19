package Models.Player;

import java.time.LocalDateTime;

public class Notification
{
	private Player senderPlayer;
	private Player receiverPlayer;
	private LocalDateTime sendingTime;
	private String message;
	//TODO
	
	public Notification(Player senderPlayer, Player receiverPlayer, String message)
	{
		this.senderPlayer = senderPlayer;
		this.receiverPlayer = receiverPlayer;
		this.sendingTime = LocalDateTime.now(); //TODO
		this.message = message;
	}
	
	public Player getSenderPlayer()
	{
		return senderPlayer;
	}
	public void setSenderPlayer(Player senderPlayer)
	{
		this.senderPlayer = senderPlayer;
	}
	public Player getReceiverPlayer()
	{
		return receiverPlayer;
	}
	public void setReceiverPlayer(Player receiverPlayer)
	{
		this.receiverPlayer = receiverPlayer;
	}
	public LocalDateTime getSendingTime()
	{
		return sendingTime;
	}
	public void setSendingTime(LocalDateTime sendingTime)
	{
		this.sendingTime = sendingTime;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
}
