package Models.Units.CombatUnits;

public class LongRange extends CombatUnit{
    private LongRangeType type;
    
    public LongRange()
    {
    
    }
    
    private void getReadyToFight(){

    }
    
    @Override
    public String toString()
    {
        return type.name();
    }
}
