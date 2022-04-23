package Models.Units.CombatUnits;

public class LongRange extends CombatUnit{
    private LongRangeType type;

    public LongRange(LongRangeType type){
        this.type = type;
    }
    
    private void getReadyToFight(){

    }
    
    @Override
    public String toString()
    {
        return type.name();
    }
}
