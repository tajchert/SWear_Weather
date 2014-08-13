package pl.tajchert.swear.api;


import java.util.HashMap;
import java.util.Map;
public class Rain {

    private double _3h;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public double get3h() {
        return _3h;
    }

    public void set3h(double _3h) {
        this._3h = _3h;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}