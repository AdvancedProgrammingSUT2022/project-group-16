package Models.TypeAdapters;

import Models.Units.Unit;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UnitTypeAdapter implements JsonSerializer<Unit>, JsonDeserializer<Unit> {
    @Override
    public JsonElement serialize(Unit unit, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(unit.getClass().getCanonicalName()));
        result.add("properties", context.serialize(unit, unit.getClass()));

        return result;
    }

    @Override
    public Unit deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String t = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(t));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
