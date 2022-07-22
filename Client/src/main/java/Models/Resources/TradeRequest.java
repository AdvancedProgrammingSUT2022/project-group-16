package Models.Resources;

import Models.Player.Civilization;
import Models.Player.Player;
import Models.User;

public class TradeRequest {
    transient private final Player sender;
    public Civilization senderCivilization;
    private final String offerToSell;
    private final String wantToBuy;

    public TradeRequest(Player sender, String offerToSell, String wantToBuy) {
        this.sender = sender;
        this.offerToSell = offerToSell;
        this.wantToBuy = wantToBuy;
    }

    public Player getSender() {
        return sender;
    }

    public String getOfferToSell() {
        return offerToSell;
    }

    public String getWantToBuy() {
        return wantToBuy;
    }
}
