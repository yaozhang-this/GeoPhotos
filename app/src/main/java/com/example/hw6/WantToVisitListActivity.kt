package com.example.hw6

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.ArrayList

class WantToVisitListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_want_to_visit_list)
        setTitle("Places I want to go")
        val listView = findViewById<ListView>(R.id.wantToVisitList)
        val sharedPreferences =
            getSharedPreferences("com.example.hw6", Context.MODE_PRIVATE)

        wantToVisit = ObjectSerializer.deserialize(
            sharedPreferences
                .getString("wantToVisit", ObjectSerializer.serialize(ArrayList<String>()))
        ) as ArrayList<String>

        if (wantToVisit == null)
        {
            wantToVisit = ArrayList<String>()
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wantToVisit)
        listView.adapter = arrayAdapter
    } //onCreate
}