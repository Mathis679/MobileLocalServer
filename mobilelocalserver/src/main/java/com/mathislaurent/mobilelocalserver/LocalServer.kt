package com.mathislaurent.mobilelocalserver

import android.content.Context
import com.mathislaurent.mobilelocalserver.model.ServerConfig
import com.mathislaurent.mobilelocalserver.model.ServerType

open class LocalServer(private val context: Context, private val events: LocalServerEvents) {

    fun createServer(config: ServerConfig) {
        when (config.type) {
            ServerType.BLUETOOTH -> {

            }
            ServerType.WIFI_DIRECT -> {

            }
            else -> {

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