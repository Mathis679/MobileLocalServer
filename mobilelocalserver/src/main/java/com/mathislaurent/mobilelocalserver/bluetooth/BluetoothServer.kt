package com.mathislaurent.mobilelocalserver.bluetooth

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID

const val NAME = "MOBILE_LOCAL_SERVER"

class BluetoothServer(private val context: Context) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    fun create() {
        checkConfig()

    }

    private fun checkConfig() {
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            throw BluetoothNotSupported()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                // User should enable bluetooth
                throw BluetoothNotEnabled()
            } else {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // User has no permission
                    throw BluetoothPermissionNotGranted()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, UUID.randomUUID())
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(BluetoothServer::class.java.name, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    // TODO handle
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(BluetoothServer::class.java.name, "Could not close the connect socket", e)
            }
        }
    }

}