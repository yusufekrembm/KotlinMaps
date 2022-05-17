package mobi.appcent.flightapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Airline (
    val iata: String,
    val icao: String,
    val name: String
): Parcelable