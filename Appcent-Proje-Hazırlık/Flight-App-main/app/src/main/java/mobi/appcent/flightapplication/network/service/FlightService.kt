package mobi.appcent.flightapplication.network.service

import mobi.appcent.flightapplication.model.Flight
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightService{
    @GET("/v1/flights")
    fun getFlights(@Query("access_key") apiKey : String) : Call<Flight>
}