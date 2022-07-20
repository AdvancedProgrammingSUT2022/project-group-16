package Models.Resources;

import Models.Player.Player;
import Models.User;

public class TradeRequest {
    private final Player sender;
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
