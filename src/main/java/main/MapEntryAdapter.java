package main;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.AbstractMap;

public class MapEntryAdapter implements JsonSerializer<Map.Entry<String, String>>, JsonDeserializer<Map.Entry<String, String>> {

    @Override
    public JsonElement serialize(Map.Entry<String, String> src, Type typeOfSrc, JsonSerializationContext context) {
     JsonObject obj = new JsonObject();
    	    
    	    // Get the key and value from src
    	    String key = src.getKey();
    	    String value = src.getValue();

    	    // Add properties to the JsonObject
    	    obj.addProperty("key", key);
    	    obj.addProperty("value", value);

    	    return obj;
    	}


    @Override
    public Map.Entry<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        // Safely retrieve key and value, returning a default value if null
        String key = obj.has("key") && !obj.get("key").isJsonNull() ? obj.get("key").getAsString() : null;
        String value = obj.has("value") && !obj.get("value").isJsonNull() ? obj.get("value").getAsString() : null;

        return new AbstractMap.SimpleEntry<>(key, value);
    }


// Register the TypeAdapter with Gson

}
