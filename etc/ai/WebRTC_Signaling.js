let express = require('express');
let http = require('http');
let app = express();
let cors = require('cors');
const fs = require('fs');
let server = http.createServer(app);
let socketio = require('socket.io');
let io = socketio(server, { path: '/ws/' });

app.use(cors());
const PORT = process.env.PORT || 5000;

let users = {};
let socketToRoom = {};

io.on('connection', (socket) => {
  socket.on('join_room', (data) => {
    const roomId = data.room;

    console.log(`[join_room] Room: ${roomId}, Socket ID: ${socket.id}`);

    if (!users[roomId]) {
      users[roomId] = [];
    }
    users[roomId].push(socket.id);
    socketToRoom[socket.id] = roomId;
    socket.join(roomId);

    const otherUsers = users[roomId].filter(id => id !== socket.id);
    socket.emit('all_users', otherUsers);

    console.log(`Users in room ${roomId}:`, users[roomId]);
  });

  socket.on('offer', (data) => {
	console.log("offer");
    socket.to(data.offerReceiveID).emit('getOffer', {
      sdp: data.sdp,
      offerSendID: socket.id,
    });
  });

  socket.on('answer', (data) => {
	console.log("answer");
    socket.to(data.answerReceiveID).emit('getAnswer', {
      sdp: data.sdp,
      answerSendID: socket.id,
    });
  });

  socket.on('candidate', (data) => {
	  console.log("onIce");
	  socket.to(data.candidateReceiveID).emit('getCandidate', {
		candidate: data.candidate,
		sdpMid: data.sdpMid,
		sdpMLineIndex: data.sdpMLineIndex,
		candidateSendID: socket.id,
		candidateReceiveID: data.candidateReceiveID,
	  });
	});

  socket.on('disconnect', () => {
	
	console.log("disconnect");
    const roomId = socketToRoom[socket.id];
    let room = users[roomId];
    if (room) {
      users[roomId] = room.filter(id => id !== socket.id);
      if (users[roomId].length === 0) {
        delete users[roomId];
      }
    }
    socket.to(roomId).emit('user_exit', { id: socket.id });
    delete socketToRoom[socket.id];

    console.log(`[EXIT] Socket ID: ${socket.id}, Room: ${roomId}`);
  });
});

server.listen(PORT, "0.0.0.0", () => {
  console.log(`Server running on port ${PORT}`);
});
