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
        println("Connected to server")
        socket.soTimeout = timeout

        input = BufferedReader(InputStreamReader(socket.getInputStream()))
        output = PrintWriter(socket.getOutputStream(), true)
    }

    //sendData(httpRequest: String
    fun sendData(){
        try {
            println("Connected to server")

            // Gửi yêu cầu HTTP GET tới server
            val httpRequest = "GET / HTTP/1.1\r\nHost: $serverAddress\r\n\r\n"
            output.print(httpRequest)
            output.flush()
            println("Sent to server: $httpRequest")

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

            socket.close()
            println("Connection closed")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun main(){
    val test = SocketClient("107.178.102.172", 3000)
    test.sendData()
}