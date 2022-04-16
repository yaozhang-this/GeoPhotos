package com.example.hw6

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

var places = ArrayList<String>()
var locations = ArrayList<LatLng>()
var arrayAdapter: ArrayAdapter<*>? = null
var latitudes = ArrayList<String>()
var longitudes = ArrayList<String>()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    } //onCreate
    fun visitedMap(view : View){
        val intent = Intent(applicationContext, MapsActivity::class.java)
        startActivity(intent)
    }
} //MainActivity