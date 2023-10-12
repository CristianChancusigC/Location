package com.moni.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class File4GNetwork(private val context: Context) {
    private val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private var describeCont = 0;
    private var signalLevel = 0;
    private var dbm = 0;
    private var rssi = 0
    private var rsrp = 0
    private var rsrq = 0
    private var rssrn = 0
    private var asuLevel = 0
    private var hashCode = 0
    private var cqi = 0
    private var timingAdvance = 0
    private var bandwidth = 0
    private var mobileNetworkOperator = ""

    fun checkPermissionTelephony(context: Context, activity: Activity) {
        val PERMISSION_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"
        val PERMISSION_ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
        val PERMISSION_REQUEST_CODE = 1

        val hasReadPhoneStatePermission = ContextCompat.checkSelfPermission(
            context,
            PERMISSION_READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            PERMISSION_ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasReadPhoneStatePermission || !hasAccessFineLocationPermission) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(PERMISSION_READ_PHONE_STATE, PERMISSION_ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            println("Correct Access")
        }
    }

    fun getLevelSignal(): Int {
        return signalLevel
    }

    fun getSignalDbm(): Int {
        return dbm
    }

    fun getRssiData(): Int {
        return rssi
    }

    fun getRsrpData(): Int {
        return rsrp
    }

    fun getRsrqData(): Int {
        return rsrq
    }

    fun getRssrnData(): Int {
        return rssrn
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkData4GNetwork(context: Context, activity: Activity) {
        val PERMISSION_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"
        val PERMISSION_ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
        val PERMISSION_REQUEST_CODE = 1

        val hasReadPhoneStatePermission = ContextCompat.checkSelfPermission(
            context,
            PERMISSION_READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            PERMISSION_ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasReadPhoneStatePermission || !hasAccessFineLocationPermission) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(PERMISSION_READ_PHONE_STATE, PERMISSION_ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Get all Cell info
            val cellInfoList: List<CellInfo> = telephonyManager.allCellInfo
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte) {
                    // Telephone methods
                    val cellSignalStrengthLte = cellInfo.cellSignalStrength
                    val cellIdentityLte = cellInfo.cellIdentity
                    println("------------------")
                    println(cellSignalStrengthLte)
                    // Get Data values of Telephone methods
                    describeCont = cellSignalStrengthLte.describeContents()
                    rssi = cellSignalStrengthLte.rssi
                    dbm = cellSignalStrengthLte.dbm
                    signalLevel = cellSignalStrengthLte.level
                    rsrp = cellSignalStrengthLte.rsrp
                    rsrq = cellSignalStrengthLte.rsrq
                    rssrn = cellSignalStrengthLte.rssnr
                    asuLevel = cellSignalStrengthLte.asuLevel
                    hashCode = cellSignalStrengthLte.hashCode()
                    cqi = cellSignalStrengthLte.cqi
                    timingAdvance = cellSignalStrengthLte.timingAdvance
                    bandwidth = cellIdentityLte.bandwidth
                    mobileNetworkOperator = cellIdentityLte.mobileNetworkOperator.toString()
                }
            }
        }
    }

    // Data for file
    fun getAllData4GNetwork(): String {
        return ", Level: $signalLevel" +
                ", Dbm: $dbm" +
                ", Rsrp: $rsrp" +
                ", Rsrq: $rsrq" +
                ", Rssi: $rssi" +
                ", Rssnr: $rssrn" +
                ", asuLevel: $asuLevel" +
                ", hashCode: $hashCode" +
                ", cqi: $cqi" +
                ", timingAdvance: $timingAdvance" +
                ", bandwidth: $bandwidth" +
                ", mobileNetworkOperator: $mobileNetworkOperator" +
                " "
    }

    @SuppressLint("MissingPermission")
    fun getNetworkType(): String {
//        Compare network type with constants
        val networkTypeString = when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO rev. 0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO rev. A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO rev. B"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            TelephonyManager.NETWORK_TYPE_GSM -> "GSM"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
            TelephonyManager.NETWORK_TYPE_IDEN -> "iDen"
            TelephonyManager.NETWORK_TYPE_IWLAN -> "IWLAN"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> "NONE"
            else -> {
                "NONE"
            }
        }
        return networkTypeString
    }
}