package com.yusufekrem.kotlinmaps.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.yusufekrem.kotlinmaps.R
import com.yusufekrem.kotlinmaps.databinding.ActivityMapsBinding
import com.yusufekrem.kotlinmaps.model.Place
import com.yusufekrem.kotlinmaps.roomdb.PlaceDao
import com.yusufekrem.kotlinmaps.roomdb.PlaceDatabase
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private  var selectedLatitude : Double? = null
    private  var selectedLongitude : Double? = null
    private lateinit var sharedPreferences : SharedPreferences
    private var trackBoolean: Boolean = false
    private lateinit var db : PlaceDatabase
    private lateinit var placeDao: PlaceDao
    val compositeDisposable = CompositeDisposable()
    var placeFromMain : Place? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        sharedPreferences = this.getSharedPreferences("com.yusufekrem.kotlinmaps", MODE_PRIVATE)
        trackBoolean = false
        db = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places")
            .build()
        placeDao = db.placeDao()
        binding.saveButton.isEnabled = false
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        val intent = intent
        val info = intent.getStringExtra("info")
        if(info=="new"){
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE



            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = LocationListener { p0 ->
                trackBoolean = sharedPreferences.getBoolean("trackBoolean",false)
                if(!trackBoolean){
                    val userLocation = LatLng(p0.latitude,p0.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                }
            }

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) { // mesaj gosterelim mi
                    Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        // request permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                }else {
                    // request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                //permission granted
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(lastLocation!= null){
                    val lastUserLocation  = LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15F))
                }
                mMap.isMyLocationEnabled = true
            }

        }else {
            mMap.clear()
            placeFromMain = intent.getSerializableExtra("selectedPlace") as? Place
            placeFromMain?.let{
                val latlng = LatLng(it.latitude,it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15F))

                binding.placeText.setText(it.name)
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
            }

        }

//         val eiffel = LatLng(48.8560881,2.2979407)
//        mMap.addMarker(MarkerOptions().position(eiffel).title("Eiffel Tower"))
//         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel,15F))
    }

      private fun registerLauncher(){
          permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
              if(it){
                  if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                      //permission granted
                      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
                      val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                      if(lastLocation!= null){
                          val lastUserLocation  = LatLng(lastLocation.latitude,lastLocation.longitude)
                          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15F))
                      }
                  }
              }else {
                  //permission denied
                  Toast.makeText(this@MapsActivity,"Permission needed",Toast.LENGTH_LONG).show()
              }
          }
      }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
        binding.saveButton.isEnabled = true
    }

    fun save(view: View){
        // Main Thread UI, Default -> CPU, IO Thread Internet/Database

        if(selectedLatitude!= null && selectedLongitude != null){
            val place = Place(binding.placeText.text.toString(),selectedLatitude!!,selectedLongitude!!)
            compositeDisposable.add(
                placeDao.insert(place)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::handleResponse)
            )
        }

    }
    private fun handleResponse(){
        val intent = Intent(this,MainActivity::class.java)
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun delete(view: View){
       placeFromMain?.let{
            compositeDisposable.delete(
                placeDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(this::handleResponse)
            )
        }
    }

    override fun onDestroy() {

        compositeDisposable.clear()
        super.onDestroy()
    }
}