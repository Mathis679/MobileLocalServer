package com.mathislaurent.mobilelocalserver

import android.content.Context
import com.mathislaurent.mobilelocalserver.bluetooth.BluetoothServer
import com.mathislaurent.mobilelocalserver.bluetooth.BluetoothServerEvents
import com.mathislaurent.mobilelocalserver.model.ServerConfig
import com.mathislaurent.mobilelocalserver.model.ServerType
import com.mathislaurent.mobilelocalserver.wifi.WifiServer
import java.io.Serializable
import java.util.UUID

class LocalServerMediator(
    private val context: Context,
    private val events: LocalServerEvents,
    private val config: ServerConfig
): LocalServer, BluetoothServerEvents {

    private val localServerList: ArrayList<LocalServer> = ArrayList()

    //region LocalServer
    override fun create() {
        when (config.type) {
            ServerType.BLUETOOTH -> {
                localServerList.add(BluetoothServer(context, this).also { it.create() })
            }
            ServerType.WIFI_DIRECT -> {
                localServerList.add(WifiServer(context).also { it.create() })
            }
            else -> {
                localServerList.add(BluetoothServer(context, this).also { it.create() })
                localServerList.add(WifiServer(context).also { it.create() })
            }
        }
        events.onServerCreated("hello")
    }

    override fun dispatch(data: Serializable) {
        localServerList.forEach {
            it.dispatch(data)
        }
    }

    override fun disposed() {
        localServerList.forEach {
            it.disposed()
        }
        localServerList.clear()
    }
    //endregion

    //region BluetoothServerEvents
    override fun onNewClient() {
        events.onNewClient()
    }

    override fun onNewSocketOpenedListening(uuid: UUID) {
        TODO("Not yet implemented")
    }
    //endregion
}

interface LocalServer {
    fun create()

    fun dispatch(data: Serializable)

    fun disposed()
}

interface LocalServerEvents {
    fun onServerCreated(uUID: String)
    fun onServerDestroyed()
    fun onNewClient()
    fun onLostClient()
}