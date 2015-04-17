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
        return codes.get(code) + ".";
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

    private static void putCodesMeanings(HashMap<Integer, String> codes){
        codes.put(200, "It is a fucking thunderstorm with rain");
        codes.put(201, "It is a fucking thunderstorm with rain");
        codes.put(202, "It is a fucking thunderstorm with rain");
        codes.put(210, "It is fucking lightning and thundering");
        codes.put(211, "It is a fucking thunderstorm");
        codes.put(212, "It is a fucking heavy thunderstorm");
        codes.put(221, "It is a fucking raging thunderstorm");
        codes.put(230, "It is a fucking thunderstorm with a drizzle");
        codes.put(231, "It is a fucking thunderstorm with a drizzle");
        codes.put(232, "It is a fucking thunderstorm with a drizzle");
        codes.put(300, "It is fucking drizzling");
        codes.put(301, "It is fucking drizzling");
        codes.put(302, "It is heavily fucking drizzling");
        codes.put(310, "It is fucking drizzling");
        codes.put(311, "It is fucking drizzling");
        codes.put(312, "It is fucking raining");
        codes.put(313, "It is fucking raining and drizzling");
        codes.put(314, "It is heavily fucking raining and drizzling");
        codes.put(321, "It is fucking drizzling");
        codes.put(500, "It is lightly fucking raining");
        codes.put(501, "It is moderately fucking raining");
        codes.put(502, "It is heavily fucking raining");
        codes.put(503, "It is fucking raining");
        codes.put(504, "It is fucking extreme rain");
        codes.put(511, "It is fucking freezing rain");
        codes.put(520, "It is lightly fucking raining");
        codes.put(521, "It is fucking showering");
        codes.put(522, "It is heavily fucking raining");
        codes.put(531, "It is raggedly fucking raining");
        codes.put(600, "It is lightly fucking snowing");
        codes.put(601, "It is fucking snowing");
        codes.put(602, "It is heavily fucking snowing");
        codes.put(611, "It is fucking sleeting");
        codes.put(612, "It is fucking sleeting");
        codes.put(615, "It is lightly fucking raining and snowing");
        codes.put(616, "It is heavily fucking raining and snowing");
        codes.put(620, "It is lightly fucking snowing");
        codes.put(621, "It is fucking snowing");
        codes.put(622, "It is heavily fucking snowing");
        codes.put(701, "It is fucking misty");
        codes.put(711, "It is fucking smoky");
        codes.put(721, "It is fucking hazy");
        codes.put(731, "It is fucking sandy with dust whirls");
        codes.put(741, "It is fucking foggy");
        codes.put(751, "It is fucking sandy");
        codes.put(761, "It is fucking dusty");
        codes.put(762, "There is fucking volcanic ash");
        codes.put(771, "There are fucking squalls");
        codes.put(781, "There is a fucking tornado");
        codes.put(800, "It is fucking awesome");
        codes.put(801, "It is fucking barely cloudy");
        codes.put(802, "It is fucking cloudy");
        codes.put(803, "It is fucking cloudy");
        codes.put(804, "It is fucking cloudy");
        codes.put(900, "There is a fucking tornado");
        codes.put(901, "There is a fucking tropical storm");
        codes.put(902, "There is a fucking hurricane");
        codes.put(903, "It is fucking cold");
        codes.put(904, "It is fucking hot");
        codes.put(905, "It is fucking windy");
        codes.put(906, "It is fucking hailing");
        codes.put(950, "It is fucking calm");
        codes.put(951, "It is fucking calm");
        codes.put(952, "It is lightly fucking breezy");
        codes.put(953, "It is gently fucking breezy");
        codes.put(954, "It is moderately fucking breezy");
        codes.put(955, "It is freshly fucking breezy");
        codes.put(956, "It is really fucking breezy");
        codes.put(957, "It is nearly fucking gale-force windy");
        codes.put(958, "It is fucking gale-force windy");
        codes.put(959, "It is severely fucking gale-force windy");
        codes.put(960, "It is fucking storming");
        codes.put(961, "It is violently fucking storming");
        codes.put(962, "There is a fucking hurricane");
    }

}
