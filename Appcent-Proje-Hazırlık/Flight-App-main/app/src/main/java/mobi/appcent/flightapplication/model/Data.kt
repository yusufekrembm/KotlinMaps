package mobi.appcent.flightapplication.model

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("aircraft")
    val aircraft: Any,
    @SerializedName("airline")
    val airline: Airline,
    @SerializedName("arrival")
    val arrival: Arrival,
    @SerializedName("departure")
    val departure: Departure,
    @SerializedName("flight")
    val flight: FFlight,
    @SerializedName("flight_date")
    val flight_date: String,
    @SerializedName("flight_status")
    val flight_status: String,
    @SerializedName("live")
    val live: Any
)