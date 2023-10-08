package com.moni.location

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellIdentityNr
import android.telephony.CellInfoNr
import android.telephony.NetworkScanRequest
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class File5GNetwork(private val context: Context) {
    private val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private var signalLevel = 0;
    private var dbm = 0;
    private var asuLevel = 0
    private var bands = intArrayOf()
    private var nci: Long = 0
    private var nrarfcn = 0
    private var pci = 0
    private var tac = 0
    private var state = 0
    private var cdmaNetworkId = 0
    private var cdmaSystemId = 0
    private var cellBandwidths = intArrayOf()
    private var channelNumber = 0
    private var duplexMode = 0
    private var isManualSelection = false
    private var isSearching = false
    private var roaming = false
    private var operatorAlphaLong = ""
    private var operatorNumeric = ""

    @RequiresApi(Build.VERSION_CODES.R)
    fun checkData5GNetwork(context: Context, activity: Activity) {
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
            val cellInfoList = telephonyManager.allCellInfo
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoNr) {
                    val cellSignalStrengthNr = cellInfo.cellSignalStrength
                    val cellIdentityNr = cellInfo.cellIdentity as CellIdentityNr
                    val serviceState = telephonyManager.serviceState
                    cellSignalStrengthNr.hashCode()
                    asuLevel = cellSignalStrengthNr.asuLevel
                    signalLevel = cellSignalStrengthNr.level
                    dbm = cellSignalStrengthNr.dbm
                    bands = cellIdentityNr.bands
                    nci = cellIdentityNr.nci
                    nrarfcn = cellIdentityNr.nrarfcn
                    pci = cellIdentityNr.pci
                    tac = cellIdentityNr.tac
                    if (serviceState != null) {
                        state = serviceState.state
                        cdmaNetworkId = serviceState.cdmaNetworkId
                        cdmaSystemId = serviceState.cdmaSystemId
                        cellBandwidths = serviceState.cellBandwidths
                        channelNumber = serviceState.channelNumber
                        duplexMode = serviceState.duplexMode
                        isManualSelection = serviceState.isManualSelection
                        isSearching = serviceState.isSearching
                        roaming = serviceState.roaming
                        operatorAlphaLong = serviceState.operatorAlphaLong
                        operatorNumeric = serviceState.operatorNumeric
                    };
                }
            }
        }
    }

    fun getAllData5GNetwork(): String {
        return ", Level: $signalLevel" +
                ", AsuLevel: $asuLevel" +
                ", Dbm: $dbm" +
                ", nci: $nci" +
                ", nrarfcn: $nrarfcn" +
                ", pci: $pci" +
                ", tac: $tac" +
                ", state: $state" +
                ", cdmaNetworkId: $cdmaNetworkId" +
                ", cdmaSystemId: $cdmaSystemId" +
                ", cellBandwidths: $cellBandwidths[0]" +
                ", channelNumber: $channelNumber" +
                ", duplexMode: $duplexMode" +
                ", isManualSelection: $isManualSelection" +
                ", isSearching: $isSearching" +
                ", roaming: $roaming" +
                ", operatorAlphaLong: $operatorAlphaLong" +
                ", operatorNumeric: $operatorNumeric" +
                " "
    }
}