package com.mathislaurent.mobilelocalserver.model

const val DEFAULT_MAX_CLIENT = 10

data class ServerConfig(
    val type: ServerType = ServerType.BOTH,
    val maxClient: Int = DEFAULT_MAX_CLIENT
)

enum class ServerType {
    WIFI_DIRECT,
    BLUETOOTH,
    BOTH
}