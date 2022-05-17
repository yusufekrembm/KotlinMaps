package mobi.appcent.flightapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_view.view.*
import mobi.appcent.flightapplication.R
import mobi.appcent.flightapplication.model.Data
import mobi.appcent.flightapplication.model.Flight
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class FlightRecyclerAdapter (private val flightList : List<Data>, var onAppcentClickListener : OnAppcentClickListener) : RecyclerView.Adapter<FlightRecyclerAdapter.ViewHolder>(){
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightRecyclerAdapter.ViewHolder {
        context = parent.context
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.item_view,parent,false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: FlightRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() : Int{
        return flightList.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(){
            val item = flightList[adapterPosition]

            itemView.tvAirline.setText(item.airline.name)
            val departure = item.departure.icao + " - " + item.departure.estimated.let { formatDate(it,"HH:mm") }

            itemView.tvDeparture.setText(departure)
            val arrival = item.arrival.icao + " - " + item.arrival.estimated.let { formatDate(it,"HH:mm")}

            itemView.tvArrival.setText(arrival)

            if (item.departure.delay != null)
                itemView.tvDelayTime.setText(item.departure.delay.toString() + "mi")
            else
                itemView.tvDelayTime.setText("0 mi")

            if (item.flight_status == "active")
                itemView.ivStatus.setImageResource(R.drawable.green_circle)
            else if (item.flight_status == "scheduled" || item.flight_status == "landed")
                itemView.ivStatus.setImageResource(R.drawable.yellow_circle)
            else
                itemView.ivStatus.setImageResource(R.drawable.red_circle)

            itemView.setOnClickListener {
                onAppcentClickListener.onClick(adapterPosition)
            }
        }
    }

}

fun formatDate(date: String,format: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.ENGLISH).parse(date)
    val formattedDate = SimpleDateFormat(format, Locale.ENGLISH)
    return (formattedDate.format(date.time))
}

interface OnAppcentClickListener {
    fun onClick(position: Int)
}
