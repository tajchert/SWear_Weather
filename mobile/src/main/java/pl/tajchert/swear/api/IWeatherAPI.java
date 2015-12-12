package pl.tajchert.swear.api;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IWeatherAPI {
    @GET("/weather")
    void getWeather(@Query("lat") double latitude, @Query("lon") double longitude, @Query("appid") String appid, Callback<WeatherAPI> callback);

}
