package com.kimnlee.common.network

import io.socket.client.IO
import io.socket.client.Socket
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.net.URISyntaxException

class SignalingClient(
    private val roomId: String
) {
    private lateinit var socket: Socket

    init {
        try {
            socket = IO.socket("https://anpr.mobipay.kr/ws")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        socket.on(Socket.EVENT_CONNECT) {
            sendJoinRoom(roomId) // Join the room on connect
        }

        socket.on("getOffer") { args ->
            val sdp = args[0] as String
            // Handle received offer
        }

        socket.on("getAnswer") { args ->
            val sdp = args[0] as String
            // Handle received answer
        }

        socket.on("getCandidate") { args ->
            val candidate = args[0] as String
            val sdpMid = args[1] as String
            val sdpMLineIndex = args[2] as Int
            // Handle ICE candidate
        }

        socket.connect()
    }

    fun sendJoinRoom(roomId: String) {
        socket.emit("join_room", mapOf("room" to roomId))
    }

    fun sendIceCandidate(candidate: IceCandidate) {
        socket.emit("candidate", mapOf(
            "candidate" to candidate.sdp,
            "sdpMid" to candidate.sdpMid,
            "sdpMLineIndex" to candidate.sdpMLineIndex
        ))
    }

    fun sendOffer(sdp: SessionDescription) {
        socket.emit("offer", mapOf(
            "sdp" to sdp.description,
            "offerReceiveID" to roomId
        ))
    }

    fun sendAnswer(sdp: SessionDescription) {
        socket.emit("answer", mapOf(
            "sdp" to sdp.description,
            "answerReceiveID" to roomId
        ))
    }

    fun disconnect() {
        socket.disconnect()
    }
}
