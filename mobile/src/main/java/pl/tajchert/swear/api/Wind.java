package pl.tajchert.swear.api;

import java.util.HashMap;
import java.util.Map;


public class Wind {

    private double speed;
    private double deg;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}