package kentang;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static kentang.GameComponent.maxClientsCount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class GomukuServerThread extends Thread {
    private DataInputStream is = null;
    private PrintStream os = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private Socket clientSocket = null;
    private GameComponent a;
    
    int id = -1;
    
    public GomukuServerThread(Socket clientSocket, GameComponent a) {
        this.clientSocket = clientSocket;
        this.a = a;
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && a.threads[i] == this) {
                id = i;
                break;
            }
        }
    }
  
    public void broadcastToRoom(String message) {
        for (int i = 0; i < a.maxClientsCount; i++) {
            if (a.threads[i] != null && a.player[id].getRoom() == a.player[i].getRoom()) {
                a.threads[i].os.println(message);
            }
        }
    }
    
    public void broadcastToRoom(String message, int roomId) {
        for (int i = 0; i < a.maxClientsCount; i++) {
            if (a.threads[i] != null && a.player[i].getRoom() == roomId) {
                a.threads[i].os.println(message);
            }
        }
    }
    
    public void broadcastToHome(String message) {
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && a.player[i].getRoom() == 0) {
                a.threads[i].os.println(message);
            }
        }
    }
    
    /****************** HANDLE REQUEST FROM CLIENT  *********************/
    
    public void createRoom(JSONObject object) {
        int roomId = ++a.roomCount;
        int playerId = ((Long) object.get("playerId")).intValue();
        
        a.playerCount[roomId]++;
        a.player[id].setRoom(roomId);
        
        JSONObject message = new JSONObject();
        message.put("type", "join");
        message.put("name", a.player[playerId].getName());
        message.put("playerId", playerId);
        message.put("roomId", roomId);
        os.println(message.toString());
        sendHomeStatus();
        sendRoomStatus();
    }
    
    public void joinRoom(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = ((Long) object.get("roomId")).intValue();
        
        if(roomId < 0 || roomId > a.roomCount || a.isPlaying[roomId]) {
            System.out.println("Masuk room gagal");
            JSONObject message = new JSONObject();
            message.put("type", "playing");
            message.put("roomId", roomId);
            os.println(message.toString());
            return;
        }
        
        a.playerCount[roomId]++;
        a.player[id].setRoom(roomId);
        boolean sendPlay = a.playerCount[roomId] == 3;
        
        JSONObject join = new JSONObject();
        join.put("type", "join");
        join.put("playerId", playerId);
        System.out.println("Dia adalah " + a.player[playerId].getName());
        join.put("name", a.player[playerId].getName());
        join.put("roomId", roomId);
        os.println(join.toString());
        
        sendHomeStatus();
        sendRoomStatus();
        
        JSONObject chat = new JSONObject();
        chat.put("type", "chat");
        chat.put("playerId", 0);
        chat.put("roomId", roomId);
        chat.put("name", "admin");
        chat.put("content", a.player[id].getName() + " entered the room.");
        broadcastToRoom(chat.toString());
        if(sendPlay) {
            sendCanPlay();
        }
    }
    
    public void exitRoom(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = a.player[playerId].getRoom();
        
        a.playerCount[roomId]--;
        a.player[id].setRoom(0);
        boolean sendDisable = a.playerCount[roomId] == 2;
        
        JSONObject join = new JSONObject();
        join.put("type", "join");
        join.put("name", a.player[playerId].getName());
        join.put("playerId", playerId);
        join.put("roomId", 0);
        os.println(join.toString());
        
        sendHomeStatus();
        sendRoomStatus(roomId);
        
        JSONObject chat = new JSONObject();
        chat.put("type", "chat");
        chat.put("playerId", 0);
        chat.put("roomId", roomId);
        chat.put("name", "admin");
        chat.put("content", a.player[id].getName() + " leave the room.");
        broadcastToRoom(chat.toString(), roomId);
        if(sendDisable) {
            sendDisablePlay(roomId);
        }
    }
    
    public void play(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        String name = a.player[playerId].getName();
        int roomId = a.player[playerId].getRoom();
        
        a.playerNow[roomId] = 0;
        a.isPlaying[roomId] = true;
        a.boards[roomId] = new Board();
        int ke = 0;
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && roomId == a.player[i].getRoom()) {
                a.players[roomId][ke] = i;
                a.virtual[i] = ke;
                ke++;
            }
        }
        
        sendPlay(playerId);
        sendTurn(a.players[roomId][0]);
        sendHomeStatus();
        sendRoomStatus();
    }
    
    public void chat(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        object.put("name", a.player[playerId].getName());
        broadcastToRoom(object.toString());
    }
    
    public void addCoordinate(JSONObject object) {
        JSONArray coordinate = (JSONArray) object.get("content");
        int x = ((Long) coordinate.get(0)).intValue();
        int y = ((Long) coordinate.get(1)).intValue();
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = ((Long) object.get("roomId")).intValue();
        int ke = a.playerNow[roomId];
        
        if(playerId != a.players[roomId][ke]) {
            JSONObject not = new JSONObject();
            not.put("type", "not");
            os.println(not.toString());
            return;
        }
        if(!a.boards[roomId].isValidMove(x, y)) {
            JSONObject chat = new JSONObject();
            chat.put("type", "chat");
            chat.put("playerId", 0);
            chat.put("roomId", roomId);
            chat.put("name", "admin");
            chat.put("content", "Your move is invalid. Either out of the box or has been occupied.");
            os.println(chat.toString());
            return;
        }
        boolean isWin = a.boards[roomId].add(x, y, playerId);
        object.put("name", a.player[playerId].getName());
        object.put("virtualId", a.virtual[playerId]);
        broadcastToRoom(object.toString());
        if(isWin) {
            unplay(roomId);
            sendWin(playerId);
            sendRoomStatus();
            sendHomeStatus();
            return;
        }
        ke = (ke + 1) % a.playerCount[roomId];
        a.playerNow[roomId] = (a.playerNow[roomId] + 1) % a.playerCount[roomId];
        sendTurn(a.players[roomId][ke]);
    }
    
    public void disconnect() {
        
    }
    
    /********************* SERVER ACTION BELOW HERE *************************/
    
    public JSONObject createHomeStatus() {
        JSONArray content = new JSONArray();
        for(int i = 0; i < a.maxRoomCount; i++) {
            if(a.playerCount[i] > 0) {
                JSONObject room = new JSONObject();
                room.put("roomId", i);
                room.put("playerCount", a.playerCount[i]);
                room.put("isPlaying", a.isPlaying[i]);
                content.add(room);
            }
        }
        JSONObject object = new JSONObject();
        object.put("content", content);
        object.put("type", "home");
        return object;
    }
    
    public JSONObject createRoomStatus() {
        JSONArray content = new JSONArray();
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && a.player[i].getRoom() == a.player[id].getRoom()) {
                JSONObject player = new JSONObject();
                player.put("playerId", i);
                player.put("name", a.player[i].getName());
                content.add(player);
            }
        }
        JSONObject object = new JSONObject();
        object.put("content", content);
        object.put("type", "room");
        return object;
    }
    
    public JSONObject createRoomStatus(int roomId) {
        JSONArray content = new JSONArray();
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && a.player[i].getRoom() == roomId) {
                JSONObject player = new JSONObject();
                player.put("playerId", i);
                player.put("name", a.player[i].getName());
                content.add(player);
            }
        }
        JSONObject object = new JSONObject();
        object.put("content", content);
        object.put("type", "room");
        return object;
    }
    
    public void sendHomeStatus() {
        JSONObject object = createHomeStatus();
        broadcastToHome(object.toString());
    }
    
    public void sendRoomStatus() {
        JSONObject object = createRoomStatus();
        broadcastToRoom(object.toString());
    }
    
    public void sendRoomStatus(int roomId) {
        JSONObject object = createRoomStatus(roomId);
        broadcastToRoom(object.toString(), roomId);
    }
    
    public void sendCanPlay() {
        JSONObject object = new JSONObject();
        object.put("type", "can");
        broadcastToRoom(object.toString());
    }
    
    public void sendDisablePlay(int roomId) {
        JSONObject object = new JSONObject();
        object.put("type", "disable");
        broadcastToRoom(object.toString(), roomId);
    }
    
    public void sendPlay(int playerId) {
        JSONObject object = new JSONObject();
        object.put("type", "play");
        object.put("playerId", playerId);
        int roomId = a.player[playerId].getRoom();
        object.put("roomId", roomId);
        object.put("name", a.player[playerId].getName());
        for(int i = 0; i < a.playerCount[roomId]; i++) {
            int toSend = a.players[roomId][i];
            object.put("virtualId", i);
            a.threads[toSend].os.println(object.toString());
        }
    }
    
    public void sendWin(int playerId) {
        JSONObject object = new JSONObject();
        object.put("type", "win");
        object.put("playerId", playerId);
        object.put("name", a.player[playerId].getName());
        broadcastToRoom(object.toString());
    }
    
    public void sendTurn(int playerId) {
        JSONObject turn = new JSONObject();
        turn.put("type", "turn");
        a.threads[playerId].os.println(turn.toString());
    }
    
    public void unplay(int roomId) {
        JSONObject object = new JSONObject();
        object.put("type", "unplay");
        a.isPlaying[roomId] = false;
        broadcastToRoom(object.toString());
    }
    
    /********************* THREAD *************************/
    
    public void run() {
        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            for(int i = 0; i < a.maxClientsCount; i++) {
                if(a.threads[i] != null && a.threads[i] == this) {
                    id = i;
                    os.println(id);
                }
            }
            
            // Get player object from client;
            ois = new ObjectInputStream(is);
            a.player[id] = (Player) ois.readObject();
            
            System.out.println("Terima player");
            
            // Send home status to connected user
            JSONObject homeStatus = createHomeStatus();
            os.println(homeStatus.toString());

            // Receive message from clients
            while (true) {
                String line = is.readLine();
                JSONObject message = new JSONObject();
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(line);
                    message = (JSONObject) obj;
                } catch (ParseException ex) {
                    System.err.println(ex);
                    break;
                }
                
                // Client close connection
                String type = (String) message.get("type");
                if (type.equals("create")) {
                    createRoom(message);
                } else if(type.equals("join")) {
                    joinRoom(message);
                } else if(type.equals("exit")) {
                    exitRoom(message);
                } else if(type.equals("play")) {
                    play(message);
                } else if(type.equals("chat")) {
                    chat(message);
                } else if(type.equals("coordinate")) {
                    addCoordinate(message);
                }
            }
            
            // User is leaving the room
            disconnect();
            
            // Remove this thread from threads
            a.threads[a.player[id].getId()] = null;
            is.close();
            os.close();
            ois.close();
            oos.close();
            clientSocket.close();
            
        } catch (Exception e) {
            disconnect();
            System.err.println(e);
            e.printStackTrace();
        }

    }
}