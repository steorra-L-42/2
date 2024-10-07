package com.kimnlee.freedrive.data

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.audio.JavaAudioDeviceModule
import java.net.URI
import java.util.concurrent.Executors

class WebRTCManager(private val context: Context) {

    // Variables for WebRTC
    private val peerConnectionFactory: PeerConnectionFactory
    private val eglBase: EglBase = EglBase.create()
    private val audioSource: AudioSource
    private val audioTrack: AudioTrack

    // Socket.IO variables
    private lateinit var socket: Socket
    private var mySocketId: String? = null
    private lateinit var roomId: String

    // PeerConnections mapping
    private val peerConnections = mutableMapOf<String, PeerConnection>()

    // Remote user IDs
    private val remoteUserIds = mutableListOf<String>()

    // Executors for threading
    private val executor = Executors.newSingleThreadExecutor()

    init {
        // Initialize PeerConnectionFactory
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        val options = PeerConnectionFactory.Options()
        val encoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setAudioDeviceModule(JavaAudioDeviceModule.builder(context).createAudioDeviceModule())
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()

        // Create audio source and track
        val audioConstraints = MediaConstraints()
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource)

        // Initialize socket connection
        initializeSocket()
    }

    private fun initializeSocket() {
        socket = IO.socket(URI.create("wss://anpr.mobipay.kr/"), IO.Options().apply {
            transports = arrayOf("websocket")
            path = "/ws/"
        })

        socket.on(Socket.EVENT_CONNECT) {
            mySocketId = socket.id()
            Log.d("WebRTCManager", "Socket connected, ID: $mySocketId")
        }

        socket.on("connect_error") { args ->
            Log.e("WebRTCManager", "Socket.IO connection error: ${args[0]}")
        }

        // Handle signaling messages
        socket.on("all_users") { args ->
            executor.execute {
                val users = args[0] as JSONArray
                Log.d("WebRTCManager", "Users in the room: $users")

                for (i in 0 until users.length()) {
                    val userID = users.getString(i)
                    if (userID != mySocketId) {
                        remoteUserIds.add(userID)
                        createPeerConnection(userID)
                    }
                }
            }
        }

        socket.on("getOffer") { args ->
            executor.execute {
                handleOffer(args[0] as JSONObject)
            }
        }

        socket.on("getAnswer") { args ->
            executor.execute {
                handleAnswer(args[0] as JSONObject)
            }
        }

        socket.on("getCandidate") { args ->
            executor.execute {
                handleCandidate(args[0] as JSONObject)
            }
        }

        socket.on("user_exit") { args ->
            executor.execute {
                val userId = (args[0] as JSONObject).getString("id")
                Log.d("WebRTCManager", "User exited: $userId")
                // Clean up peer connection
                val peerConnection = peerConnections.remove(userId)
                peerConnection?.close()
            }
        }

        socket.connect()
    }

    fun joinRoom(roomId: String) {
        this.roomId = roomId

        // Emit the join_room event to the server
        val joinMessage = JSONObject().apply {
            put("room", roomId)
        }
        socket.emit("join_room", joinMessage)
    }

    private fun createPeerConnection(remoteUserId: String): PeerConnection? {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }
        val peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(signalingState: PeerConnection.SignalingState?) {
                Log.d("WebRTCManager", "Signaling state changed: $signalingState")
            }

            override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState?) {
                Log.d("WebRTCManager", "ICE connection state changed: $iceConnectionState")
            }

            override fun onIceConnectionReceivingChange(receiving: Boolean) {
                Log.d("WebRTCManager", "ICE connection receiving change: $receiving")
            }

            override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState?) {
                Log.d("WebRTCManager", "ICE gathering state changed: $iceGatheringState")
            }

            override fun onIceCandidate(candidate: IceCandidate?) {
                candidate?.let {
                    val candidateMessage = JSONObject().apply {
                        put("candidate", it.sdp)
                        put("sdpMid", it.sdpMid)
                        put("sdpMLineIndex", it.sdpMLineIndex)
                        put("candidateSendID", mySocketId)
                        put("candidateReceiveID", remoteUserId)
                    }
                    Log.d("WebRTCManager", "Sending ICE candidate to $remoteUserId: ${candidate.sdp}")
                    socket.emit("candidate", candidateMessage)
                }
            }

            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {
                Log.d("WebRTCManager", "ICE candidates removed")
            }

            override fun onAddStream(stream: MediaStream?) {
                Log.d("WebRTCManager", "Stream added (deprecated): $stream")
                // This method is deprecated in Unified Plan
            }

            override fun onRemoveStream(stream: MediaStream?) {
                Log.d("WebRTCManager", "Stream removed (deprecated): $stream")
                // This method is deprecated in Unified Plan
            }

            override fun onDataChannel(dataChannel: DataChannel?) {
                Log.d("WebRTCManager", "Data channel received: $dataChannel")
            }

            override fun onRenegotiationNeeded() {
                Log.d("WebRTCManager", "Renegotiation needed")
            }

            override fun onTrack(transceiver: RtpTransceiver?) {
                Log.d("WebRTCManager", "Track added: ${transceiver?.receiver?.track()?.id()}")

                // Handle remote audio track
                val remoteTrack = transceiver?.receiver?.track() as? AudioTrack
                remoteTrack?.setEnabled(true)
                // Set up an audio sink if needed
            }
        })

        // Add audio track to the peer connection using addTrack
        val streamId = "ARDAMS"
        val rtpSender = peerConnection?.addTrack(audioTrack, listOf(streamId))

        if (peerConnection != null) {
            peerConnections[remoteUserId] = peerConnection
        }
        return peerConnection
    }

    fun startCall() {
        for (remoteUserId in remoteUserIds) {
            val peerConnection = peerConnections[remoteUserId]
            peerConnection?.createOffer(object : SdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    peerConnection.setLocalDescription(this, sdp)
                    val offerMessage = JSONObject().apply {
                        put("sdp", sdp?.description)
                        put("offerSendID", mySocketId)
                        put("offerReceiveID", remoteUserId)
                    }
                    socket.emit("offer", offerMessage)
                }

                override fun onSetSuccess() {}

                override fun onCreateFailure(p0: String?) {
                    Log.e("WebRTCManager", "Failed to create offer: $p0")
                }

                override fun onSetFailure(p0: String?) {
                    Log.e("WebRTCManager", "Failed to set local description: $p0")
                }
            }, MediaConstraints())
        }
    }

    private fun handleOffer(message: JSONObject) {
//        val sdp = message.getString("sdp")
        val sdp = message.optString("sdp", null)
        if (sdp == null) {
            Log.e("WebRTCManager", "Received null SDP in offer/answer")
            return
        }

        val offerSendID = message.getString("offerSendID")
        val offer = SessionDescription(SessionDescription.Type.OFFER, sdp)

        var peerConnection = peerConnections[offerSendID]
        if (peerConnection == null) {
            peerConnection = createPeerConnection(offerSendID)
        }

        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                Log.d("WebRTCManager", "Remote description set successfully for offer")
                createAnswer(offerSendID)
            }

            override fun onSetFailure(error: String?) {
                Log.e("WebRTCManager", "Failed to set remote description for offer: $error")
            }

            override fun onCreateSuccess(p0: SessionDescription?) {}

            override fun onCreateFailure(p0: String?) {}
        }, offer)
    }

    private fun createAnswer(remoteUserId: String) {
        val peerConnection = peerConnections[remoteUserId]
        peerConnection?.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                peerConnection.setLocalDescription(this, sdp)
                val answerMessage = JSONObject().apply {
                    put("sdp", sdp?.description)
                    put("answerSendID", mySocketId)
                    put("answerReceiveID", remoteUserId)
                }
                socket.emit("answer", answerMessage)
            }

            override fun onSetSuccess() {}

            override fun onCreateFailure(p0: String?) {
                Log.e("WebRTCManager", "Failed to create answer: $p0")
            }

            override fun onSetFailure(p0: String?) {
                Log.e("WebRTCManager", "Failed to set local description: $p0")
            }
        }, MediaConstraints())
    }

    private fun handleAnswer(message: JSONObject) {
//        val sdp = message.getString("sdp")
        val sdp = message.optString("sdp", null)
        if (sdp == null) {
            Log.e("WebRTCManager", "Received null SDP in offer/answer")
            return
        }
        val answerSendID = message.getString("answerSendID")
        val answer = SessionDescription(SessionDescription.Type.ANSWER, sdp)

        val peerConnection = peerConnections[answerSendID]
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                Log.d("WebRTCManager", "Remote description set successfully for answer")
            }

            override fun onSetFailure(error: String?) {
                Log.e("WebRTCManager", "Failed to set remote description for answer: $error")
            }

            override fun onCreateSuccess(p0: SessionDescription?) {}

            override fun onCreateFailure(p0: String?) {}
        }, answer)
    }

    private fun handleCandidate(message: JSONObject) {
        Log.d("WebRTCManager", "Received candidate message: $message")
        val sdpMid = message.optString("sdpMid", null)
        val sdpMLineIndex = message.optInt("sdpMLineIndex", -1)
        val candidateStr = message.optString("candidate", null)
        val candidateSendID = message.optString("candidateSendID", null)

        if (sdpMid == null || sdpMLineIndex == -1 || candidateStr == null || candidateSendID == null) {
            Log.e("WebRTCManager", "Invalid candidate message received")
            return
        }

        val candidate = IceCandidate(sdpMid, sdpMLineIndex, candidateStr)
        val peerConnection = peerConnections[candidateSendID]
        if (peerConnection == null) {
            Log.e("WebRTCManager", "PeerConnection not found for ID: $candidateSendID")
            return
        }
        peerConnection.addIceCandidate(candidate)
    }

    fun hangup() {
        for ((_, peerConnection) in peerConnections) {
            peerConnection.close()
        }
        peerConnections.clear()
        socket.disconnect()
    }
}
