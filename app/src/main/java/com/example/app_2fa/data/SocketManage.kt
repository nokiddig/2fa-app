package com.example.app_2fa.data

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.example.app_2fa.utils.Constants
import com.example.app_2fa.utils.SaveData
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SocketManager(serverAddress: String = "107.178.102.172:3000") {
    private val socket: Socket = IO.socket("http://$serverAddress")
    //live data
    private val _loginState = MutableStateFlow(0)
    private val _twoFAState = MutableStateFlow(SaveData.IS_2FA)
    private val _keyState = MutableStateFlow("")
    private val _code2FAState = MutableStateFlow("")
    private var connectStatus = false

    val loginState: StateFlow<Int> get() = _loginState
    val twoFAState: StateFlow<Boolean> get() = _twoFAState
    val keyState: StateFlow<String> get() = _keyState
    val code2FAState: StateFlow<String> get() = _code2FAState

    // Khởi tạo các listener
    private val onConnect = Emitter.Listener {
        connectStatus = true
        Log.d("bug_2fa","Connected to socket server")
    }

    private val onDisconnect = Emitter.Listener {
        connectStatus = false
        Log.d("bug_2fa","Disconnected from socket server")

    }

    private val onError = Emitter.Listener { args ->
        connectStatus = false
        val error = args[0] as Exception
        error.printStackTrace()
    }

    private val onMessage = Emitter.Listener { args ->
        val message = args[0] as String
        Log.d("bug_2fa", "Received message -${SYSTEM_MODE}-: $message")
        when (SYSTEM_MODE){
            Constants.MODE_LOGIN -> {
                if (message.isDigitsOnly()) {
                    _loginState.value = message.toInt()
                }
            }

            Constants.MODE_BAT_2FA -> {

            }

            Constants.MODE_TAT_2FA -> {
                if (message == "1") {
                    _twoFAState.value = false
                }

            }

            Constants.MODE_XAC_MINH_2FA -> {
                if (SaveData.IS_LOGIN){

                }
                else {
                    SYSTEM_MODE = Constants.MODE_LOGIN
                    if (message.isDigitsOnly() && message.toInt() == 1){
                        _loginState.value = 1
                    }
                }

            }

            Constants.MODE_XAC_MINH_BAT_2FA -> {
                if (message == "1") {
                    _twoFAState.value = true

                    Log.d("bug_2fa", "trueeeeeeeeeeeee")
                }
            }

            Constants.MODE_REGISTER -> {

            }
        }
    }

    init {
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket.on("message", onMessage)
        socket.connect()
        Thread.sleep(3000)
        sendMessage("tat2fa user1 12345")
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
        if (!connectStatus){
            connect()
            Thread.sleep(3000)
        }
        SYSTEM_MODE = message.split(" ").first()
        socket.emit("message", message)
    }

    companion object {
        var SYSTEM_MODE = ""
        @Volatile
        private var instance: SocketManager? = null

        fun getInstance(): SocketManager {
            return instance ?: synchronized(this) {
                instance ?: SocketManager().also { instance = it }
            }
        }
    }
}