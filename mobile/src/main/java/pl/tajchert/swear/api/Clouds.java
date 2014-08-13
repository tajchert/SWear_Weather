package pl.tajchert.swear.api;

import java.util.HashMap;
import java.util.Map;

public class Clouds {

    private int all;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}