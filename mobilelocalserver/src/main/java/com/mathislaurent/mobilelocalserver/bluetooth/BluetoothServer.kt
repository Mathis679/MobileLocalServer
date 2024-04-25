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
import com.mathislaurent.mobilelocalserver.LocalServer
import com.mathislaurent.mobilelocalserver.utils.toByteArray
import java.io.IOException
import java.io.Serializable
import java.util.UUID

const val NAME = "MOBILE_LOCAL_SERVER"

class BluetoothServer(private val context: Context, private val events: BluetoothServerEvents): LocalServer {

    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val sockets: ArrayList<BluetoothSocket> = ArrayList()

    override fun create() {
        checkConfig()
        addNewSocket()
    }

    fun addNewSocket() {
        AcceptThread().start()
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

    override fun dispatch(data: Serializable) {
        sockets.forEach {
            it.outputStream.write(data.toByteArray())
        }
    }

    override fun disposed() {
        TODO("Not yet implemented")
    }



    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            val uuid = generateNewUUID()
            events.onNewSocketOpenedListening(uuid)
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, uuid)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(BluetoothServer::class.java.name, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    events.onNewClient()
                    sockets.add(it)
                    shouldLoop = false
                }
            }
        }
    }

    private fun generateNewUUID(): UUID {
        return UUID.randomUUID()
    }

}

interface BluetoothServerEvents {
    fun onNewClient()

    fun onNewSocketOpenedListening(uuid: UUID)
}