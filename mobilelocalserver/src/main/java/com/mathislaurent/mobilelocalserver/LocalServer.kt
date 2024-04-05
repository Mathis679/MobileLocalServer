package com.mathislaurent.mobilelocalserver

import android.content.Context
import com.mathislaurent.mobilelocalserver.bluetooth.BluetoothServer
import com.mathislaurent.mobilelocalserver.model.ServerConfig
import com.mathislaurent.mobilelocalserver.model.ServerType

open class LocalServer(private val context: Context, private val events: LocalServerEvents) {

    private var bluetoothServer: BluetoothServer? = null

    fun createServer(config: ServerConfig) {
        when (config.type) {
            ServerType.BLUETOOTH -> {
                bluetoothServer = BluetoothServer(context).also { it.create() }
            }
            ServerType.WIFI_DIRECT -> {

            }
            else -> {
                bluetoothServer = BluetoothServer(context).also { it.create() }
            }
        }
        events.onServerCreated("")
    }
}

interface LocalServerEvents {
    fun onServerCreated(UUID: String)
    fun onServerDestroyed()
    fun onNewClient()
    fun onLostClient()
}