package com.example.hw6

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

class VisitedListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited_list)

        val listView = findViewById<ListView>(R.id.visitedList)
        val sharedPreferences =
            getSharedPreferences("com.example.hw6", Context.MODE_PRIVATE)


        places = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("places", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>
        latitudes = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("lats", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>
        longitudes = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("lons", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>

        if (places.size > 0 && latitudes.size > 0 && longitudes.size > 0) {
            for (i in latitudes.indices) {
                locations.add(LatLng(latitudes[i].toDouble(), longitudes[i].toDouble()))
            }
        } else {
            locations.add(LatLng(0.0, 0.0))
            places.add("Add a new place...")
            latitudes.add("0")
            longitudes.add("0")
        }//endif

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, places)
        listView.adapter = arrayAdapter

    } // onCreate
}