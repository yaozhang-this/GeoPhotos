package com.example.hw6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        setTitle("Summary")
        var numberOfPlaces = findViewById<TextView>(R.id.numberOfPlaces)
        var countriesText = findViewById<TextView>(R.id.countries)

        if (places.size == 0){
            countriesText.text=""
            numberOfPlaces.text = "Start Traveling! You haven't been to any places yet!"
        }
        else {
            countriesText.text = "You have been to countries of ${countries.distinct()}"
            numberOfPlaces.text = "Wow! You have been to ${places.size} different places"
        }

    }
}