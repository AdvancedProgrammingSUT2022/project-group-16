package Models.Units.CombatUnits;

public class MidRange extends CombatUnit{
    private MidRangeType type;

    public MidRange(MidRangeType type){
        this.type = type;
    }
    
    private void pillage(){

    }
    
    @Override
    public String toString()
    {
        return type.name();
    }

    @Override
    protected void move() {

    }
}
