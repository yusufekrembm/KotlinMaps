package mobi.appcent.flightapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Arrival(
    val airport: String,
    val timezone: String,
    val iata: String,
    val icao: String,
    val terminal: String?,
    val gate: String?,
    val baggage: String?,
    val delay: String?,
    val scheduled: String,
    val estimated: String,
    val actual: String?,
    val estimated_runway: String?,
    val actual_runway: String?,
): Parcelable