package pl.tajchert.swear;


import android.content.Context;

import java.util.HashMap;

import pl.tajchert.swear.api.WeatherAPI;

public class WeatherToTextConverter {

    public static String getText(Context context, WeatherAPI weatherNow){
        String response =  "";
        //By weather code, from openweathermap.org/weather-conditions
        try {
            response += getTextByCode(weatherNow.getWeather().get(0).getId());
        } catch (Exception e) {
            //get gave null
            return "";
        }
        response = checkForAwesomeConditions(context, weatherNow, response);
        return response;
    }

    private static String getTextByCode(int code){
        HashMap<Integer, String> codes = new HashMap<Integer, String>();
        putCodesMeanings(codes);
        return " " + codes.get(code);
    }

    private static String checkForAwesomeConditions(Context context, WeatherAPI weatherNow, String response){
        int code;
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
        return response;
    }

    private static String getTemperatureText(Context context, double temp){
        if( temp < 0 ){
            return context.getString(R.string.swear_text_cold);
        } else if ( temp > 28 ){
            return context.getString(R.string.swear_text_hot);
        }
        return "";
    }

    private static void putCodesMeanings(HashMap<Integer, String> codes){
        codes.put(200, "It is a fucking thunderstorm with rain outside");
        codes.put(201, "It is a fucking thunderstorm with rain outside");
        codes.put(202, "It is a fucking thunderstorm with heavy rain outside");
        codes.put(210, "It is fucking lightning and thundering outside");
        codes.put(211, "It is a fucking thunderstorm outside");
        codes.put(212, "It is a fucking heavy thunderstorm outside");
        codes.put(221, "It is a fucking raging thunderstorm outside");
        codes.put(230, "It is a fucking thunderstorm with a drizzle outside");
        codes.put(231, "It is a fucking thunderstorm with a drizzle outside");
        codes.put(232, "It is a fucking thunderstorm with a drizzle outside");
        codes.put(300, "It is fucking drizzling outside");
        codes.put(301, "It is fucking drizzling outside");
        codes.put(302, "It is heavily fucking drizzling outside");
        codes.put(310, "It is fucking drizzling outside");
        codes.put(311, "It is fucking drizzling outside");
        codes.put(312, "It is fucking raining outside");
        codes.put(313, "It is fucking raining and drizzling outside");
        codes.put(314, "It is heavily fucking raining and drizzling outside");
        codes.put(321, "It is fucking drizzling outside");
        codes.put(500, "It is lightly fucking raining outside");
        codes.put(501, "It is moderately fucking raining outside");
        codes.put(502, "It is heavily fucking raining outside");
        codes.put(503, "It is fucking raining outside");
        codes.put(504, "It is fucking extreme rain outside");
        codes.put(511, "It is fucking freezing rain outside");
        codes.put(520, "It is lightly fucking raining outside");
        codes.put(521, "It is fucking showering outside");
        codes.put(522, "It is heavily fucking raining outside");
        codes.put(531, "It is raggedly fucking raining outside");
        codes.put(600, "It is lightly fucking snowing outside");
        codes.put(601, "It is fucking snowing outside");
        codes.put(602, "It is heavily fucking snowing outside");
        codes.put(611, "It is fucking sleeting outside");
        codes.put(612, "It is fucking sleeting outside");
        codes.put(615, "It is lightly fucking raining and snowing outside");
        codes.put(616, "It is heavily fucking raining and snowing outside");
        codes.put(620, "It is lightly fucking snowing outside");
        codes.put(621, "It is fucking snowing outside");
        codes.put(622, "It is heavily fucking snowing outside");
        codes.put(701, "It is fucking misty outside");
        codes.put(711, "It is fucking smoky outside");
        codes.put(721, "It is fucking hazy outside");
        codes.put(731, "It is fucking sandy with dust whirls outside");
        codes.put(741, "It is fucking foggy outside");
        codes.put(751, "It is fucking sandy outside");
        codes.put(761, "It is fucking dusty outside");
        codes.put(762, "There is fucking volcanic ash outside");
        codes.put(771, "There are fucking squalls outside");
        codes.put(781, "There is a fucking tornado outside");
        codes.put(800, "It is fucking awesome outside");
        codes.put(801, "It is fucking barely cloudy outside");
        codes.put(802, "It is fucking cloudy outside");
        codes.put(803, "It is fucking cloudy outside");
        codes.put(804, "It is fucking cloudy outside");
        codes.put(900, "There is a fucking tornado outside");
        codes.put(901, "There is a fucking tropical storm outside");
        codes.put(902, "There is a fucking hurricane outside");
        codes.put(903, "It is fucking cold outside");
        codes.put(904, "It is fucking hot outside");
        codes.put(905, "It is fucking windy outside");
        codes.put(906, "It is fucking hailing outside");
        codes.put(950, "It is fucking calm outside");
        codes.put(951, "It is lightly fucking airy outside");
        codes.put(952, "It is lightly fucking breezy outside");
        codes.put(953, "It is gently fucking breezy outside");
        codes.put(954, "It is moderately fucking breezy outside");
        codes.put(955, "It is freshly fucking breezy outside");
        codes.put(956, "It is really fucking breezy outside");
        codes.put(957, "Is is nearly fucking gale-force windy outside");
        codes.put(958, "It is fucking gale-force windy outside");
        codes.put(959, "It is severely fucking gale-force windy outside");
        codes.put(960, "It is fucking storming outside");
        codes.put(961, "It is violently fucking storming outside");
        codes.put(962, "There is a fucking hurricane outside");
    }

}
