package com.example.app_2fa.data

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class SocketManager(serverAddress: String) {
    private val socket: Socket = IO.socket("http://$serverAddress")

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
    }

    init {
        // Gán các listener cho socket
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket.on("message", onMessage)
    }

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun sendMessage(message: String) {
        connect()
        Thread.sleep(5000)

//        val jsonMessage = JsonObject().apply {
//            addProperty("message", message)
//        }
        socket.emit("message", message)
        Thread.sleep(5000)
        Log.d("bug_2fa", "send message: $message")
        disconnect()
    }
}

//fun main() {
//    val serverAddress = "107.178.102.172:3000" // Thay đổi thành địa chỉ server của bạn
//    val socketManager = SocketManager(serverAddress)
//    socketManager.connect()
//
//    // Gửi dữ liệu sau khi kết nối
//    socketManager.sendMessage("Hello, server!")
//}
