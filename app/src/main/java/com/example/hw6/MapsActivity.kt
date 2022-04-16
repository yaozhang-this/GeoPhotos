package com.example.hw6

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hw6.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap //set up google
    private lateinit var binding: ActivityMapsBinding//set up by google
    var lastKnownLocation: Location? = null
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }//onCreateOptionsMenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.visitedList -> {
                intent = Intent(applicationContext, VisitedListActivity::class.java)
                startActivity(intent)
            }
            R.id.wantToVisit -> {
                intent = Intent(applicationContext, WantToVisitListActivity::class.java)
                startActivity(intent)
            }
            R.id.summary -> {
                intent = Intent(applicationContext, SummaryActivity::class.java)
                startActivity(intent)
            }
            R.id.clear -> {
                val sharedPreferences = getSharedPreferences("com.example.hw6", MODE_PRIVATE)
                wantToVisit.clear()
                wLatitudes.clear()
                wLongitudes.clear()
                sharedPreferences.edit().remove("wantToVisit").apply();
                sharedPreferences.edit().remove("wLatitudes").apply();
                sharedPreferences.edit().remove("wLongitudes").apply();
                intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }

        }
        return true
    } //end onOptionsItemSelected

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0f,
                    locationListener!!
                )

                lastKnownLocation =
                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val option = intent.getIntExtra("option", 0)
                if (option == 0) readAll(getRealPathFromURI(MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                else choosePhotos()
                readSavedLocations()

            }//if
        }//outter if
    }//onRequestPermissionsResult


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }//onCreate


    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager?;
        locationListener = object : LocationListener {
            @Override
            override fun onLocationChanged(location: Location) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}

        }// locationListener = object

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0F,
                locationListener!!
            )
            lastKnownLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val option = intent.getIntExtra("option", 0)
            if (option == 0) readAll(getRealPathFromURI(MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            else choosePhotos()
            readSavedLocations()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        }

    }//onMapReady

    private fun choosePhotos() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startForResult.launch(intent)
    }

    //https://stackoverflow.com/questions/18590514/loading-all-the-images-from-gallery-into-the-application-in-android
    @RequiresApi(Build.VERSION_CODES.S)
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                places.clear()
                countries.clear()
                var geocoder = Geocoder(applicationContext, Locale.getDefault())
                val intent = result.data?.clipData
                val count = intent?.itemCount
                val sb = StringBuilder()
                var address = ""
                for (i in 0..count!! - 1) {
                    val path = getRealPathFromURI(intent.getItemAt(i)?.uri)
                    System.out.println(path)
                    val file = File(path[0])
                    val exif = ExifInterface(file)
                    val array = FloatArray(2)
                    exif.getLatLong(array)
                    //default value is 0/0
                    val userLocation = LatLng(array[0].toDouble(), array[1].toDouble())
                    mMap.addMarker(MarkerOptions().position(userLocation).title("test"))
                    val listAddresses = geocoder.getFromLocation(
                        array[0].toDouble(),
                        array[1].toDouble(), 1
                    )
                    val country = listAddresses[0].countryName
                    countries.add(country)
                    if (listAddresses != null && listAddresses.size > 0) {
                        if (listAddresses[0].thoroughfare != null) {
                            if (listAddresses[0].subThoroughfare != null) {
                                address += listAddresses[0].subThoroughfare + " "
                            }
                            address += listAddresses[0].thoroughfare
                            address += ", $country"
                        }
                    }//if (listAdddresses)
                    if (address == "") {
                        //val sdf = SimpleDateFormat("HH:mm yyyy-MM-dd")
                        //address += sdf.format(Date())
                        address += "unnamed location"
                        address += ", $country"
                    }
                    places.add(address)
                    val formatterDate = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    val formatterTime = java.text.SimpleDateFormat("hh:mm:ss", Locale.US)
                    sb.append(
                        "Photo $i: lat: " + array[0] + " long: " + array[1] + " date: " + formatterDate.format(
                            exif.dateTimeOriginal
                        ) + " time: " + formatterTime.format(exif.dateTimeOriginal) + "\n"
                    )
                }

            }
        }

    fun getRealPathFromURI(contentUri: Uri?): ArrayList<String> {
        val galleryURLs = ArrayList<String>()
        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var uri = contentUri
        val cursor: Cursor = managedQuery(
            uri,
            proj,  // Which columns to return
            null,  // WHERE clause; which rows to return (all rows)
            null,  // WHERE clause selection arguments (none)
            MediaStore.Images.Media.DEFAULT_SORT_ORDER
        ) // Order-by clause (ascending by name)
        for (i in 0..cursor.count - 1) {
            cursor.moveToPosition(i)
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            galleryURLs.add(cursor.getString(dataColumnIndex))
        }
        return galleryURLs
    }//https://stackoverflow.com/questions/18590514/loading-all-the-images-from-gallery-into-the-application-in-android

    @RequiresApi(Build.VERSION_CODES.S)
    fun readAll(arrayList: ArrayList<String>) {
        var geocoder = Geocoder(applicationContext, Locale.getDefault())

        if (arrayList.size == 0) {
            //text.setText("No photos detected!")
            return
        }
        val sb = StringBuilder()
        places.clear()
        countries.clear()
        for (i in 0..arrayList.size - 1) {
            var address = ""
            val path = arrayList[i]
            System.out.println(path)
            val file = File(path)
            val exif = ExifInterface(file)
            val array = FloatArray(2)
            //if no latlong data, dont read them
            if (!exif.getLatLong(array)) continue
            //default value is 0/0
            val userLocation = LatLng(array[0].toDouble(), array[1].toDouble())
            mMap.addMarker(MarkerOptions().position(userLocation).title("test"))
            val listAddresses = geocoder.getFromLocation(
                array[0].toDouble(),
                array[1].toDouble(), 1
            )
            val country = listAddresses[0].countryName
            countries.add(country)
            if (listAddresses != null && listAddresses.size > 0) {
                if (listAddresses[0].thoroughfare != null) {
                    if (listAddresses[0].subThoroughfare != null) {
                        address += listAddresses[0].subThoroughfare + " "
                    }
                    address += listAddresses[0].thoroughfare
                    address += ", $country"
                }
            }//if (listAdddresses)
            if (address == "") {
                //val sdf = SimpleDateFormat("HH:mm yyyy-MM-dd")
                //address += sdf.format(Date())
                address += "unnamed location"
                address += ", $country"
            }
            places.add(address)
            val formatterDate = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
            val formatterTime = java.text.SimpleDateFormat("hh:mm:ss", Locale.US)
            sb.append(
                "Photo $i: lat: " + array[0] + " long: " + array[1] + " date: " + formatterDate.format(
                    exif.dateTimeOriginal
                ) + " time: " + formatterTime.format(exif.dateTimeOriginal) + "\n"
            )
        }
        //text.setText(sb.toString())
    } //readAll

    override fun onMapLongClick(latLng: LatLng) {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        var address = ""


        val listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val country = listAddresses[0].countryName
        if (listAddresses != null && listAddresses.size > 0) {
            if (listAddresses[0].thoroughfare != null) {
                if (listAddresses[0].subThoroughfare != null) {
                    address += listAddresses[0].subThoroughfare + " "
                }
                address += listAddresses[0].thoroughfare
                address += ", $country"
            }
        }//if (listAdddresses)


        mMap.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        if (address == "") {
            //val sdf = SimpleDateFormat("HH:mm yyyy-MM-dd")
            //address += sdf.format(Date())
            address += "unnamed location"
            address += ", $country"
        }
        wantToVisit.add(address)
        wLatitudes.add(latLng.latitude.toString())
        wLongitudes.add(latLng.longitude.toString())
        val sharedPreferences =
            getSharedPreferences("com.example.hw6", MODE_PRIVATE)

        sharedPreferences.edit().putString("wantToVisit", ObjectSerializer.serialize(wantToVisit))
            .apply();
        sharedPreferences.edit().putString("wLatitudes", ObjectSerializer.serialize(wLatitudes))
            .apply();
        sharedPreferences.edit().putString("wLongitudes", ObjectSerializer.serialize(wLongitudes))
            .apply();
        Toast.makeText(this, "$address saved", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, WantToVisitListActivity::class.java)
        startActivity(intent)

    }//onMapLongClick

    fun readSavedLocations() {
        val sharedPreferences =
            getSharedPreferences("com.example.hw6", MODE_PRIVATE)
        wantToVisit = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("wantToVisit", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>
        wLatitudes = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("wLatitudes", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>
        wLongitudes = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("wLongitudes", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>

        for (i in 0..wLatitudes.size - 1) {
            val latLng = LatLng(wLatitudes[i].toDouble(), wLongitudes[i].toDouble())
            mMap.addMarker(
                MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
        }
    } //readSavedLocations

    fun locateMe(view: View) {
        val placeLocation = lastKnownLocation
        System.out.println(placeLocation)
        if (placeLocation != null) {
            val userLocation = LatLng(placeLocation.latitude, placeLocation.longitude)
            mMap.addMarker(
                MarkerOptions().position(userLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
        }
    } //locateMe

} //MapsActivity