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
            if(a.threads[i] == this) {
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
    
    public void broadcastToHome(String message) {
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && a.player[i].getRoom() == 0) {
                a.threads[i].os.println(message);
            }
        }
    }
    
    /****************** HANDLE REQUEST FROM CLIENT  *********************/
    
    public void createRoom(JSONObject object) {
        
    }
    
    public void joinRoom(JSONObject object) {
        
    }
    
    public void exitRoom(JSONObject object) {
        
    }
    
    public void play(JSONObject object) {
        
    }
    
    public void chat(JSONObject object) {
        
    }
    
    public void addCoordinate(JSONObject object) {
        JSONArray coordinate = (JSONArray) object.get("content");
        int x = ((Long) coordinate.get(0)).intValue();
        int y = ((Long) coordinate.get(1)).intValue();
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = ((Long) object.get("roomId")).intValue();
        a.boards[roomId].add(x, y, playerId);
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
        return object;
    }
    
    public JSONObject createRoomStatus() {
        JSONArray content = new JSONArray();
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.player[i].getRoom() == a.player[id].getRoom()) {
                JSONObject player = new JSONObject();
                player.put("playerId", i);
                player.put("name", a.player[i].getName());
                content.add(player);
            }
        }
        JSONObject object = new JSONObject();
        object.put("content", content);
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
    
    public void sendCanPlay() {
        JSONObject object = new JSONObject();
        object.put("type", "can");
        broadcastToRoom(object.toString());
    }
    
    public void sendDisablePlay() {
        JSONObject object = new JSONObject();
        object.put("type", "disable");
        broadcastToRoom(object.toString());
    }
    
    public void play(int playerId) {
        JSONObject object = new JSONObject();
        object.put("type", "play");
        object.put("playerId", playerId);
        object.put("name", a.player[playerId].getName());
        broadcastToRoom(object.toString());
    }
    
    public void win(int playerId) {
        JSONObject object = new JSONObject();
        object.put("type", "win");
        object.put("playerId", playerId);
        object.put("name", a.player[playerId].getName());
        broadcastToRoom(object.toString());
    }
    
    /**
     * Handle this socket if disconnect
     * Action : unplay
     */
    public void disconnect() {
        JSONObject object = new JSONObject();
        object.put("type", "unplay");
        broadcastToRoom(object.toString());
        sendRoomStatus();
    }
    
    /********************* THREAD *************************/
    
    public void run() {
        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            // Get player object from client;
            ois = new ObjectInputStream(is);
            a.player[id] = (Player) ois.readObject();
            
            // Send id player object to client
            oos = new ObjectOutputStream(os);
            for(int i = 0; i < a.maxClientsCount; i++) {
                if(a.threads[i] == this) {
                    a.player[id].setId(i);
                    oos.writeObject(new Integer(i));
                    break;
                }
            }
            
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
                }
                
                // Client close connection
                String type = (String) message.get("type");
                if (!type.equals("end")) {
                    broadcastToRoom(message.toString());
                } else {
                    break;
                }
            }
            
            // User is leaving the room
            broadcastToRoom(a.player[id].getName() + " is leaving the room");
            
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
        }

    }
}