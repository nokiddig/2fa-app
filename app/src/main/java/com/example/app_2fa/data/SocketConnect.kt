package com.example.app_2fa.data

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SocketConnect {
    fun connect(){
        val serverAddress = "107.178.102.172"
        val port = 3000

        try {
            // Tạo một socket kết nối tới server tại địa chỉ và cổng chỉ định
            val socket = Socket(serverAddress, port)
            println("Connected to server")

            // Tạo luồng đầu vào và đầu ra để gửi và nhận dữ liệu
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = PrintWriter(socket.getOutputStream(), true)

            // Gửi dữ liệu tới server
            val messageToSend = "Hello, server!"
            output.println(messageToSend)
            println("Sent to server: $messageToSend")

            // Nhận dữ liệu từ server
            val response = input.readLine()
            println("Received from server: $response")

            // Đóng kết nối
            socket.close()
            println("Connection closed")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
fun main(){
    var test = SocketConnect()
    test.connect();
}