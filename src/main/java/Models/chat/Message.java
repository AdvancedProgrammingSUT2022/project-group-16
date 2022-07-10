package Models.chat;

import Models.User;

public class Message {
    private User sender;
    private User receiver;
    private String time;
    private String message;


    public Message(User sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }
}
