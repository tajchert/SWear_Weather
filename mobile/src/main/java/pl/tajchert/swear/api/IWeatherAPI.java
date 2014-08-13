package pl.tajchert.swear.api;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IWeatherAPI {
    @GET("/weather")
    void getWeather(@Query("lat") double latitude, @Query("lon") double longitude, Callback<WeatherAPI> callback);

}
