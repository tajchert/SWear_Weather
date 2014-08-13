package pl.tajchert.swearcommon;


import android.location.Location;

public class Tools {

    public static final String KEY_BUNDLE_LOCATION = "swear.location";
    public static final String SEPARATOR = "<||>";

    public static final String PREFS = "pl.tajchert.swear";
    public static final String PREFS_LAST_LOCATION = "pl.tajchert.swear.lastlocation";

    public static final String WEAR_KEY_SWEAR_TEXT = "swear_text";
    public static final String WEAR_PATH = "/swear_data";

    public static final String DATA_CHANGED_ACTION = "pl.tajchert.swear.datachanged";

    public static String locationToString(Location location){
        String result = "";
        result += location.getLatitude()+Tools.SEPARATOR;
        result += location.getLongitude()+Tools.SEPARATOR;
        result += location.getAccuracy()+Tools.SEPARATOR;
        result += location.getProvider();
        return result;
    }

    public static Location locationFromString(String input){
        if(input == null){
            return null;
        }
        String arr[] = input.split(SEPARATOR);
        Location location = null;
        try {
            location = new Location(arr[3]);
            location.setLatitude(Double.parseDouble(arr[0]));
            location.setLongitude(Double.parseDouble(arr[1]));
            location.setAccuracy(Float.parseFloat(arr[2]));
        } catch (NumberFormatException e) {
            location = null;
        }
        return location;
    }
}
