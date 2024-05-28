package com.example.app_2fa.data

import android.util.Log
import com.example.app_2fa.utils.SaveData
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SocketManager(serverAddress: String = "107.178.102.172:3000") {
    private val socket: Socket = IO.socket("http://$serverAddress")
    //live data
    private val _loginState = MutableStateFlow(false)
    private val _twoFAState = MutableStateFlow(false)
    private val _keyState = MutableStateFlow("")
    private val _code2FAState = MutableStateFlow("")

    val loginState: StateFlow<Boolean> get() = _loginState
    val twoFAState: StateFlow<Boolean> get() = _twoFAState
    val keyState: StateFlow<String> get() = _keyState
    val code2FAState: StateFlow<String> get() = _code2FAState

    // Khởi tạo các listener
    private val onConnect = Emitter.Listener {
        Log.d("bug_2fa","Connected to socket server")
    }

    private val onDisconnect = Emitter.Listener {
        Log.d("bug_2fa","Disconnected from socket server")

    }

    private val onError = Emitter.Listener { args ->
        val error = args[0] as Exception
        error.printStackTrace()
    }

    private val onMessage = Emitter.Listener { args ->
        val message = args[0] as String
        Log.d("bug_2fa", "Received message: $message")
        if (SaveData.IS_LOGIN ) {

        }
        else {
            if (message.substring(0,2) == "10") {
                _loginState.value = true
            }
            if (message.substring(0,2) == "11") {
                _twoFAState.value = true
            }
        }

    }

    init {
        // Gán các listener cho socket
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket.on("message", onMessage)
    }

    fun connect() {
        Log.d("bug_2fa", "Connecting")
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun sendMessage(message: String) {
        Log.d("bug_2fa", "send message: $message")
        connect()
        Thread.sleep(3000)
        socket.emit("message", message)
        Thread.sleep(3000)
        disconnect()
    }

    companion object {
        var SYSTEM_MODE = ""
    }
}