package Models.chat;

import Models.User;

public class publicMessage {
    private final String sender;
    private String message;

    public publicMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

}
