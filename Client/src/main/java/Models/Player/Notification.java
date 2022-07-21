package Models.Player;

public class Notification {
    private int sendingTurn; // turn of sending the notification
    private String message;
    //TODO

    public Notification(Player rulerPlayer, int sendingTurn, String message) {
        this.sendingTurn = sendingTurn;
        this.message = message;
        rulerPlayer.getNotifications().add(this);
    }

    public int getSendingTurn() {
        return sendingTurn;
    }

    public String getMessage() {
        return message;
    }
}
