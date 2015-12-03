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
    
    private Player player;
    
    public GomukuServerThread(Socket clientSocket, GameComponent a) {
        this.clientSocket = clientSocket;
        this.a = a;
    }
    
    public Player getPlayer() {
        return player;
    }
  
    public void broadcastToRoom(String message) {
        for (int i = 0; i < a.maxClientsCount; i++) {
            if (a.threads[i] != null && player.getRoom() == a.threads[i].getPlayer().getRoom()) {
                a.threads[i].os.println(message);
            }
        }
    }
    
    public void broadcastToHome(String message) {
        for(int i = 0; i < a.maxClientsCount; i++) {
            if(a.threads[i] != null && a.threads[i].getPlayer().getRoom() == 0) {
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
    
    public void chat(JSONObject object) {
        
    }
    
    public void addCoordinate(JSONObject object) {
        
    }
    
    /********************* SERVER ACTION BELOW HERE *************************/
    
    public JSONObject createHomeStatus() {
        JSONObject object = new JSONObject();
        
        return object;
    }
    
    public JSONObject createRoomStatus() {
        
    }
    
    public void sendHomeStatus() {
        
    }
    
    public void sendRoomStatus() {
        
    }
    
    public void sendCanPlay() {
        
    }
    
    public void sendDisablePlay() {
        
    }
    
    public void play() {
        
    }
    
    public void win(int playerId) {
        
    }
    
    /**
     * Handle this disconnect
     */
    public void disconnect() {
        
    }
    
    public void run() {
        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            // Get player object from client;
            ois = new ObjectInputStream(is);
            player = (Player) ois.readObject();
            
            // Send id player object to client
            oos = new ObjectOutputStream(os);
            for(int i = 0; i < a.maxClientsCount; i++) {
                if(a.threads[i] == this) {
                    player.setId(i);
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
            broadcastToRoom(player.getName() + " is leaving the room");
            
            // Remove this thread from threads
            a.threads[player.getId()] = null;
            


        } catch (Exception e) {
            disconnect();
            System.err.println(e);
        }
        is.close();
        os.close();
        ois.close();
        oos.close();
        clientSocket.close();
    }
}