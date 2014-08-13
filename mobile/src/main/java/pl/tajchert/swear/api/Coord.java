package pl.tajchert.swear.api;


import java.util.HashMap;
import java.util.Map;

public class Coord {

    private double lon;
    private double lat;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public double getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}