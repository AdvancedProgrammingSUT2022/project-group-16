package Models.chat;

import Models.User;
import javafx.scene.Node;

public class Message {
    private final String sender;
    private final String receiver;
    private String message;


    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
