package Models.Units;

public enum UnitState {
    ACTIVE("✅"),
    SLEEPING("💤"),
    FORTIFIED("🛡"),
    ALERT("🔔"),
    FORTIFIED_FOR_HEALING("\uD83D\uDC89"),
    IS_SET("🎯"), // used for siege units
    IS_WORKING("\uD83D\uDEE0️"),
    HOSTAGE("\uD83C\uDFF4\u200D☠️");
    //TODO: add more states if needed


    public final String symbol;

    UnitState(String symbol) {
        this.symbol = symbol;
    }
}
