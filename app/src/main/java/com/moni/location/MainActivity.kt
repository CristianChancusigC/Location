package com.moni.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.moni.location.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    // Declaration of variables
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var fileHandler: FileHandler
    private lateinit var file4GNetwork: File4GNetwork
    private lateinit var file5GNetwork: File5GNetwork
    private var activeNetwork4G: Boolean = true
    private var activeNetwork5G: Boolean = false
    var flagStart = false
    var timer: Timer? = null
    private val period = 1000L // 1 seg
    private var dateNameFile = ""
    private val data4GNetworkArray = mutableListOf<String>()
    private val data5GNetworkArray = mutableListOf<String>()

    private var accuracy = 0f
    private var altitude = 0.0
    private var latitude = 0.0
    private var longitude = 0.0
    private var provider = ""
    private var speed = 0f
    private var dataStrLocation = "location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        file4GNetwork = File4GNetwork(this@MainActivity)
        file5GNetwork = File5GNetwork(this@MainActivity)
        fileHandler = FileHandler(this@MainActivity)
        initListener()
        checkAllPermission()
    }

    private fun checkAllPermission() {
        //checkPermissionLocation()
        file4GNetwork.checkPermissionTelephony(this, this)
    }

    private fun initListener() {
        binding.btnStart.setOnClickListener { startGetData() }
        binding.btnStop.setOnClickListener { stopGetData() }
        binding.switchNetwork4G.setOnCheckedChangeListener { _, value -> activeNetwork4G = value }
        binding.switchNetwork5G.setOnCheckedChangeListener { _, value -> activeNetwork5G = value }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun run() {
                if (flagStart) {
                    getAllDataNetwork()
                } else {
                    timer?.cancel()
                }
            }
        }, 0L, period)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllDataNetwork() {
        getCurrentLocation()
        if (activeNetwork4G && activeNetwork5G) {
            file4GNetwork.checkData4GNetwork(this, this)
            showDataNetwork()
            data4GNetworkArray.add(getStringDataNetwork("4G") + " \n")
            file5GNetwork.checkData5GNetwork(this, this)
            data5GNetworkArray.add(getStringDataNetwork("5G") + " \n")
        } else if (activeNetwork4G) {
            file4GNetwork.checkData4GNetwork(this, this)
            showDataNetwork()
            data4GNetworkArray.add(getStringDataNetwork("4G") + " \n")
        } else if (activeNetwork5G) {
            file5GNetwork.checkData5GNetwork(this, this)
            data5GNetworkArray.add(getStringDataNetwork("5G") + " \n")
        } else {
            messageToast("Select one network")
            flagStart = false
        }
    }

    private fun getCurrentDateTime(): Pair<String, String> {
        val currentDateTime = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        val currentDate = currentDateTime.format(dateFormatter)
        val currentTime = currentDateTime.format(timeFormatter)

        return Pair(currentDate, currentTime)
    }

    private fun startGetData() {
        flagStart = true
        binding.btnStart.isEnabled = false
        binding.btnStop.isEnabled = true
        binding.switchNetwork4G.isEnabled = false
        binding.switchNetwork5G.isEnabled = false
        val (fileDate, fileTime) = getCurrentDateTime()
        dateNameFile = "$fileDate-$fileTime"
        messageToast("Start Data")
        val introFile = "-----$fileDate-$fileTime----- \n"
        if (activeNetwork4G && activeNetwork5G) {
            data4GNetworkArray.add(introFile)
            data5GNetworkArray.add(introFile)
        } else if (activeNetwork4G) {
            data4GNetworkArray.add(introFile)
        } else if (activeNetwork5G) {
            data5GNetworkArray.add(introFile)
        } else {
            returnInitialStateButtons("Select one network")
        }
        startTimer()
    }

    private fun stopGetData() {
        returnInitialStateButtons("Stop Data")
        if (activeNetwork4G && activeNetwork5G) {
            saveDataApp("$dateNameFile-Data4G.txt", data4GNetworkArray)
            saveDataApp("$dateNameFile-Data5G.txt", data5GNetworkArray)
        } else if (activeNetwork4G) {
            saveDataApp("$dateNameFile-Data4G.txt", data4GNetworkArray)
        } else if (activeNetwork5G) {
            saveDataApp("$dateNameFile-Data5G.txt", data5GNetworkArray)
        } else {
            messageToast("Select one network")
            flagStart = false
        }
        data4GNetworkArray.clear()
        data5GNetworkArray.clear()
    }

    private fun returnInitialStateButtons(message: String){
        messageToast(message)
        flagStart = false
        binding.btnStop.isEnabled = false
        binding.btnStart.isEnabled = true
        binding.switchNetwork4G.isEnabled = true
        binding.switchNetwork5G.isEnabled = true
    }

//    private fun checkPermissionLocation() {
//        if (checkPermission()) {
//            if (isLocationEnable()) {
//                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
//                    val location: Location? = task.result
//                    if (location == null) {
//                        messageToast("Null Data")
//                    } else {
//                        messageToast("Get Success")
//                    }
//                }
//            } else {
//                messageToast("Turn on location")
//                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermission()
//        }
//    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnable()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        returnInitialStateButtons("Null Data")
                    } else {
                        accuracy = location.accuracy
                        altitude = location.altitude
                        latitude = location.latitude
                        longitude = location.longitude
                        provider = location.provider.toString()
                        speed = location.speed
                        runOnUiThread {
                            binding.txtValueLatitude.text = location.latitude.toString()
                            binding.txtValueLongitude.text = location.longitude.toString()
                        }
                        dataStrLocation =
                            "Accuracy: $accuracy, altitude: $altitude, latitude: $latitude, " +
                                    "longitude: $longitude, provider: $provider, speed: $speed, "
                    }
                }
            } else {
                messageToast("Turn on location")
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                messageToast("Granted")
                getCurrentLocation()
            } else {
                messageToast("Denied")
            }
        }
    }

    private fun saveDataApp(nameFile: String, listData: List<String>) {
        if (fileHandler.saveToFileInExternalStorage(nameFile, listData)) {
            messageToast("Success Save $nameFile")
        } else {
            messageToast("Error Save")
        }
    }

    private fun showDataNetwork() {
        runOnUiThread {
            println("Data")
            binding.txtValueTypeNetWork.text = file4GNetwork.getNetworkType()
            binding.txtValueLevel.text = file4GNetwork.getLevelSignal().toString()
            binding.txtValueDbm.text = file4GNetwork.getSignalDbm().toString()
            binding.txtValueRssi.text = file4GNetwork.getRssiData().toString()
            binding.txtValueRssnr.text = file4GNetwork.getRssrnData().toString()
            binding.txtValueRsrq.text = file4GNetwork.getRsrqData().toString()
            binding.txtValueRsrp.text = file4GNetwork.getRsrpData().toString()
            println("4G: $activeNetwork4G y 5G: $activeNetwork5G ")
        }
    }

    private fun getStringDataNetwork(networkType: String): String {
        val (_, currentTime) = getCurrentDateTime()
        var allData = "$currentTime : " +
                "NetworkType: " + file4GNetwork.getNetworkType() + ", " +
                dataStrLocation + " "
        allData += when (networkType) {
            "4G" -> {
                file4GNetwork.getAllData4GNetwork()
            }

            "5G" -> {
                file5GNetwork.getAllData5GNetwork()
            }

            else -> {
                file4GNetwork.getAllData4GNetwork()
            }
        }
        return allData
    }

    private fun messageToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}