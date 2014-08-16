package pl.tajchert.swear;


import android.content.Context;

import java.util.HashMap;

import pl.tajchert.swear.api.WeatherAPI;

public class WeatherToTextConverter {

    public static String getText(Context context, WeatherAPI weatherNow){
        String response = "";
        response = context.getString(R.string.swear_text_beggining);
        //By weather code, from openweathermap.org/weather-conditions
        try {
            response += getTextByCode(context, weatherNow.getWeather().get(0).getId());
        } catch (Exception e) {
            //get gave null
            return "";
        }
        //Temperature if can fit and is not already there
        if((!response.contains("hot") || !response.contains("cold")) && response.length() < 50) {
            response += getTemperatureText(context, (weatherNow.getMain().getTemp() / 273.15));
        }
        response = checkForAwesomeConditions(context, weatherNow, response);
        response += " outside.";
        return response;
    }

    private static String getTextByCode(Context context, int code){
        HashMap<Integer, String> codes = new HashMap<Integer, String>();
        putCodesMeanings(codes);
        return " " + codes.get(code);
    }

    private static String checkForAwesomeConditions(Context context, WeatherAPI weatherNow, String response){
        int code = 0;
        try {
            code = weatherNow.getWeather().get(0).getId();
        } catch (Exception e) {
            //get gave null
            return response;
        }
        double temp = weatherNow.getMain().getTemp() / 273.15;
        if(( code == 800 || code == 801 ) && temp >= 21 && temp <= 24 ) {
            response = context.getString(R.string.swear_text_awesome);
        }
        return  response;
    }

    private static String getTemperatureText(Context context, double temp){
        if( temp < 0 ){
            return context.getString(R.string.swear_text_cold);
        } else if ( temp > 28 ){
            return context.getString(R.string.swear_text_hot);
        }
        return "";
    }

    private static void putCodesMeanings(HashMap codes){
        codes.put(200, "thunderstorm with light rain");
        codes.put(201, "thunderstorm with rain");
        codes.put(202, "thunderstorm with heavy rain");
        codes.put(210, "light thunderstorm");
        codes.put(211, "thunderstorm");
        codes.put(212, "heavy thunderstorm");
        codes.put(221, "ragged thunderstorm");
        codes.put(230, "thunderstorm with light drizzle");
        codes.put(231, "thunderstorm with drizzle");
        codes.put(232, "thunderstorm with heavy drizzle");
        codes.put(300, "light intensity drizzle");
        codes.put(301, "drizzle");
        codes.put(302, "heavy intensity drizzle");
        codes.put(310, "light intensity drizzle rain");
        codes.put(311, "drizzle rain");
        codes.put(312, "heavy intensity drizzle rain");
        codes.put(313, "shower rain and drizzle");
        codes.put(314, "heavy shower rain and drizzle");
        codes.put(321, "shower drizzle");
        codes.put(500, "light rain");
        codes.put(501, "moderate rain");
        codes.put(502, "heavy intensity rain");
        codes.put(503, "very heavy rain");
        codes.put(504, "extreme rain");
        codes.put(511, "freezing rain");
        codes.put(520, "light intensity shower rain");
        codes.put(521, "shower rain");
        codes.put(522, "heavy intensity shower rain");
        codes.put(531, "ragged shower rain");
        codes.put(600, "light snow");
        codes.put(601, "snow");
        codes.put(602, "heavy snow");
        codes.put(611, "sleet");
        codes.put(612, "shower sleet");
        codes.put(615, "light rain and snow");
        codes.put(616, "rain and snow");
        codes.put(620, "light shower snow");
        codes.put(621, "shower snow");
        codes.put(622, "heavy shower snow");
        codes.put(701, "mist");
        codes.put(711, "smoke");
        codes.put(721, "haze");
        codes.put(731, "sand, dust whirls");
        codes.put(741, "fog");
        codes.put(751, "sand");
        codes.put(761, "dust");
        codes.put(762, "volcanic ash");
        codes.put(771, "squalls");
        codes.put(781, "tornado");
        codes.put(800, "clear sky");
        codes.put(801, "few clouds");
        codes.put(802, "scattered clouds");
        codes.put(803, "broken clouds");
        codes.put(804, "overcast clouds");
        codes.put(900, "tornado");
        codes.put(901, "tropical storm");
        codes.put(902, "hurricane");
        codes.put(903, "cold");
        codes.put(904, "hot");
        codes.put(905, "windy");
        codes.put(906, "hail");
        codes.put(950, "setting");
        codes.put(951, "calm");
        codes.put(952, "light breeze");
        codes.put(953, "gentle breeze");
        codes.put(954, "moderate breeze");
        codes.put(955, "fresh breeze");
        codes.put(956, "strong breeze");
        codes.put(957, "high wind, near gale");
        codes.put(958, "gale");
        codes.put(959, "severe gale");
        codes.put(960, "storm");
        codes.put(961, "violent storm");
        codes.put(962, "hurricane");
    }

}
