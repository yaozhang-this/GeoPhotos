package com.example.hw6
//HW6 by
//Alexandra Giorno + Yao Zhang
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList
import android.Manifest
import android.net.Uri
import android.opengl.Visibility
import android.provider.MediaStore
import android.widget.*
import java.io.File
import java.text.SimpleDateFormat

var places = ArrayList<String>()
var wantToVisit = ArrayList<String>()
var countries = ArrayList<String>()
var wLatitudes = ArrayList<String>()
var wLongitudes = ArrayList<String>()

class MainActivity : AppCompatActivity() {
    var checksum = arrayOf(0,0)
    lateinit var grantButton : Button
    lateinit var exploreButton : Button
    lateinit var photoText : TextView
    lateinit var mediaText : TextView
    lateinit var radioGroup: RadioGroup
    var importOption = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Geo My Photos")
        grantButton = findViewById(R.id.grantPermissionButton)
        exploreButton = findViewById(R.id.exploreButton)
        photoText = findViewById(R.id.photoAccessText)
        mediaText = findViewById(R.id.mediaLocationAccessText)
        exploreButton.visibility = View.INVISIBLE
        radioGroup = findViewById(R.id.buttonGroup)
        radioGroup.visibility = View.INVISIBLE
        verifyPermission(Manifest.permission.ACCESS_MEDIA_LOCATION, 0)
        verifyPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1)

    } //onCreate
    fun explore(view : View){
        System.out.println(importOption)
        val intent = Intent(applicationContext, MapsActivity::class.java)
        intent.putExtra("option", importOption)
        startActivity(intent)
    } //explore

    fun verifyPermission(permission: String, requestCode: Int) {
        System.out.println(permission)
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            if (permission == Manifest.permission.ACCESS_MEDIA_LOCATION) {
                mediaText.setText("Media Location Access: X")
                checksum[0] = 0
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), 0)
            }
            else{
                photoText.setText("Photo Access: X")
                grantButton.visibility = View.VISIBLE
                checksum[1] = 0
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), 1)
            }
        }
        else{
            if (permission == Manifest.permission.ACCESS_MEDIA_LOCATION) {
                mediaText.setText("Media Location Access: ✅")
                checksum[0] = 1

            }
            else{
                photoText.setText("Photo Access: ✅")

                grantButton.visibility = View.INVISIBLE
                checksum[1] = 1
            }
            if (checksum[0] == 1 && checksum[1] == 1)
            {
                radioGroup.visibility = View.VISIBLE
                exploreButton.visibility = View.VISIBLE
            }

        }
    } //verifyPermission

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            photoText.setText("Photo Access: ✅")
            checksum[1] = 1
            grantButton.visibility = View.INVISIBLE
        }
        else{
            photoText.setText("Photo Access: X")
            checksum[1] = 0
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mediaText.setText("Media Location Access: ✅")
            checksum[0] = 1

        }
        else{
            mediaText.setText("Media Location Access: X")
            checksum[0] = 0
        }
        if (checksum[0] == 1 && checksum[1] == 1)
        {
            radioGroup.visibility = View.VISIBLE
            exploreButton.visibility = View.VISIBLE
        }


    } //onRequestPermissionsResult

    fun chooseImportOption(view: View?){
        if (view != null) {
            when(view.getId()) {
                R.id.importAll -> importOption = 0
                R.id.importSelected -> importOption = 1
            }
        }//when
    }//chooseStyle

    fun grantPermission(view: View){
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), 0)
    } //grantPermission


} //MainActivity