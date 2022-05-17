package mobi.appcent.flightapplication.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import mobi.appcent.flightapplication.R
import mobi.appcent.flightapplication.adapter.FlightRecyclerAdapter
import mobi.appcent.flightapplication.adapter.OnAppcentClickListener
import mobi.appcent.flightapplication.model.Data
import mobi.appcent.flightapplication.model.Flight
import mobi.appcent.flightapplication.network.NetworkHelper
import mobi.appcent.flightapplication.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private var flightList = mutableListOf<Data>()
    private var networkHelper = NetworkHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getFlightData()
        setRecyclerAdapter()

        ibExit.setOnClickListener {
            clearSavedData()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun clearSavedData() {
        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("email").commit()
        sharedPreferences.edit().remove("password").commit()
        sharedPreferences.edit().remove("isRemember").commit()
    }

    private fun getFlightData() {
        networkHelper.flightService?.getFlights(Constant.apiKey)?.enqueue(object :
            Callback<Flight> {
            override fun onResponse(call: Call<Flight>, response: Response<Flight>) {
                response.body()?.data.let {
                    it?.forEach { flightList.add(it) }
                }
                rvFlightList.adapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<Flight>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Şu an görüntülenemiyor.", Toast.LENGTH_LONG)
                    .show()
            }

        })
    }

    private fun setRecyclerAdapter() {
        rvFlightList.layoutManager = LinearLayoutManager(this)
        rvFlightList.adapter = flightList?.let {
            FlightRecyclerAdapter(it, object : OnAppcentClickListener {
                override fun onClick(position: Int) {
                    val detailIntent = Intent(this@HomeActivity,DetailActivity::class.java)

                    detailIntent.putExtra("flight_departure",flightList[position].departure)
                    detailIntent.putExtra("flight_arrival",flightList[position].arrival)
                    detailIntent.putExtra("flight_flight",flightList[position].flight.iata)

                    startActivity(detailIntent)
                }
            })
        }
    }
}