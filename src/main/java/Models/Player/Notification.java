package Models.Player;

public class Notification
{
	private final Player senderPlayer;
	private final Player receiverPlayer;
	private int sendingTurn; // turn of sending the notification
	private String message;
	//TODO
	
	public Notification(Player senderPlayer, Player receiverPlayer, int sendingTurn, String message)
	{
		this.senderPlayer = senderPlayer;
		this.receiverPlayer = receiverPlayer;
		this.sendingTurn = sendingTurn;
		this.message = message;
	}
	
	public Player getSenderPlayer()
	{
		return senderPlayer;
	}
	public Player getReceiverPlayer()
	{
		return receiverPlayer;
	}
	public int getSendingTurn()
	{
		return sendingTurn;
	}
	public String getMessage()
	{
		return message;
	}
}
