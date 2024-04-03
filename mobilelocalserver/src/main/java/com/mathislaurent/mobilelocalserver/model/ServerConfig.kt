package com.mathislaurent.mobilelocalserver.model

data class ServerConfig(
    val type: ServerType = ServerType.BOTH
)

enum class ServerType {
    WIFI_DIRECT,
    BLUETOOTH,
    BOTH
}