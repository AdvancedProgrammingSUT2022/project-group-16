package Models.Terrain;

import com.diogonunes.jcolor.Attribute;

public enum BorderType {
    NONE(Attribute.NONE()),
    RIVER(Attribute.BACK_COLOR(0, 200, 255));

    public final Attribute attribute;

    BorderType(Attribute attribute) {
        this.attribute = attribute;
    }
}
