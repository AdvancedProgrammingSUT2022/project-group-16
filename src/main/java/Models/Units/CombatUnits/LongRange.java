package Models.Units.CombatUnits;

public class LongRange extends CombatUnit{
    private LongRangeType type;
    private boolean isSet = false;

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public LongRangeType getType() {
        return type;
    }

    public void setType(LongRangeType type) {
        this.type = type;
    }

    public LongRange(LongRangeType type){
        this.type = type;
    }
    
    private void getReadyToFight(){
        this.isSet = true;
    }
    
    @Override
    public String toString()
    {
        return type.name();
    }


}
