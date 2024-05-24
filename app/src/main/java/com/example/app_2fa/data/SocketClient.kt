package com.example.app_2fa.data
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketTimeoutException


class SocketClient(private val serverAddress: String, private val port: Int) {
    private lateinit var socket: Socket
    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter
    init {
        connect()
    }

    @Throws(Exception::class)
    fun connect(timeout: Int = 5000) {
        socket = Socket(serverAddress, port)
        //socket.soTimeout = timeout
        input = BufferedReader(InputStreamReader(socket.getInputStream()))
        output = PrintWriter(socket.getOutputStream(), true)
    }

    fun sendData(message: String){
        try {
            // Xây dựng yêu cầu HTTP GET
            val httpRequest = "GET / HTTP/1.1\r\nHost: $serverAddress\r\n\r\n"

            // Gửi yêu cầu HTTP tới server
            output = PrintWriter(socket.getOutputStream(), true)
            output.println(httpRequest)
            println("Sent to server:\n$httpRequest")
            output.println(message)
            println("Sent message to server: $message")
            emit("message", message)

            // Nhận phản hồi từ server
            var response = ""
            var line: String? = input.readLine()
            while (line != null) {
                response += line + "\n"
                line = input.readLine()
            }
            println("Received from server: $response")
            println("===============================")
            // Đóng kết nối
            socket.close()
            println("Connection closed")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //sendData(httpRequest: String
    fun sendData(){
        try {
            println("Connected to server")

            var message = "ABC"
            // Gửi yêu cầu HTTP GET tới server
            val httpRequest = "GET / HTTP/1.1\r\nHost: $serverAddress\r\nContent-Length: ${message.toByteArray().size}\r\nContent-Type: text/html\r\n$message\r\n"
            output.print(httpRequest)
            output.flush()
            println("Sent to server: $httpRequest")

            emit("message", message)
            try {
                var responseLine: String? = input.readLine()
                while (responseLine != null) {
                    println("Received from server: $responseLine")
                    responseLine = input.readLine()
                }
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
                println("Socket timed out while waiting for the server response")
            }

            //socket.close()
            println("Connection closed")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emit(event: String?, message: String?) {
        val jsonData = "{\"message\": \"[$message]\"}"

        // Gửi dữ liệu JSON qua socket

        // Gửi dữ liệu JSON qua socket
        output.println(jsonData)
//        val json = JSONObject()
//        json.put("event", event)
//        json.put("message", message)
//        output.println(Gson().toJson(json))
    }

}


fun main(){
    val test = SocketClient("107.178.102.172", 3000)
    test.sendData("login")
}

