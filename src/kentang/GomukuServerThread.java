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
  
    /*
     * Broadcast message to all clients
     */
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
    
    public void createRoom() {
        
    }
    
    public void joinRoom() {
        
    }
    
    public void exitRoom() {
        
    }
    
    public void play() {
        
    }
    
    public void chat(String message) {
        
    }
    
    public void run() {
        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            // Send player id to client;
            for (int i = 0; i < a.maxClientsCount; i++) {
                if (a.threads[i] == this) {
                    os.println(i);
                }
            }
            
            // Get player object from client;
            ObjectInputStream ois = new ObjectInputStream(is);
            player = (Player) ois.readObject();
            
            // Send id player object to client
            ObjectOutputStream oos = new ObjectOutputStream(os);
            int ret = -1;
            for(int i = 0; i < a.maxClientsCount; i++) {
                if(a.threads[i] == this) {
                    ret = i;
                    break;
                }
            }
            oos.writeObject(new Integer(ret));
            
            // Send welcome message to connected user
            JSONObject welcomeMessage = new JSONObject();
            welcomeMessage.put("type", "chat");
            welcomeMessage.put("playerId", player.getId());
            welcomeMessage.put("roomId", player.getRoom());
            welcomeMessage.put("content", player.getName() + " has entered the chat room");
            
            // Broadcast message to all clients
            broadcastToRoom(welcomeMessage.toString());

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
            
            is.close();
            os.close();
            clientSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }
}