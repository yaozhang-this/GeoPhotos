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
        setTitle("Visited Places")
        val listView = findViewById<ListView>(R.id.visitedList)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, places)
        listView.adapter = arrayAdapter

    } // onCreate
}