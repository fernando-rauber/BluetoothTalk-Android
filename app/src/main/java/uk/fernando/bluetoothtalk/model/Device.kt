package uk.fernando.bluetoothtalk.model

data class Device(val name: String, val address: String, val isConnected: Boolean = false)