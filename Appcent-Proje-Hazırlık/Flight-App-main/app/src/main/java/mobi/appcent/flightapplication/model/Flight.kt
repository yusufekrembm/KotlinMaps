package mobi.appcent.flightapplication.model

import com.google.gson.annotations.SerializedName



data class Flight (
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("pagination")
    val pagination : Pagination
)


