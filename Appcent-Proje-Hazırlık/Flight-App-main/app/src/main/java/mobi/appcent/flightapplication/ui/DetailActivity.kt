package mobi.appcent.flightapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*
import mobi.appcent.flightapplication.R
import mobi.appcent.flightapplication.model.Arrival
import mobi.appcent.flightapplication.model.Departure
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val departure: Departure? = intent.getParcelableExtra("flight_departure")
        val arrival: Arrival? = intent.getParcelableExtra("flight_arrival")
        val flight: String? = intent.getStringExtra("flight_flight")

        tvDeparture.text = departure?.iata
        tvDetailArrival.text = arrival?.iata
        tvDepartureAirportName.text = departure?.airport
        tvArrivalAirportName.text = arrival?.airport
        tvDepartureDate.text = departure?.estimated?.let {

            formatDate(
                it, "EEE, MMM d\nHH:mm"
            )
        }
        tvArrivalDate.text = arrival?.estimated?.let {
            formatDate(
                it, "EEE, MMM d\nHH:mm"
            )
        }
        tvFlightName.text = flight
        tvGateName.text = departure?.gate
        tvTerminalName.text = departure?.terminal
        tvDelayName.text = departure?.delay
        tvTitle.text = flight + " Flight Details"

        ivDetailCancel.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

    }
    fun formatDate(date: String,format: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.ENGLISH).parse(date)
        val formattedDate = SimpleDateFormat(format, Locale.ENGLISH)
        return (formattedDate.format(date.time))
    }

}