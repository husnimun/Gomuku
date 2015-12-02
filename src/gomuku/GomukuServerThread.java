package gomuku;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    
    private GomukuServerThread[] threads;
    private int maxClientsCount;
    
    private Player player;
    private static int maxRoomCount = 20;
    private static int[] playerNow = new int[maxRoomCount];
    
    public GomukuServerThread(Socket clientSocket, GomukuServerThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        
        for (int i = 0; i < playerNow.length; i++) {
            playerNow[i] = 0;
        }
    }
    
    public Player getPlayer() {
        return player;
    }
  
    /*
     * Broadcast message to all clients
     */
    public void broadcastMessage(String message) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] != null && player.getRoom() == threads[i].getPlayer().getRoom()) {
                threads[i].os.println(message);
            }
        }
    }
    
    public void run() {
        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            // Send player id to client;
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    os.println(i);
                }
            }
            
            // Get player object from client;
            ObjectInputStream ois = new ObjectInputStream(is);
            player = (Player) ois.readObject();
            
            // Send welcome message to connected user
            JSONObject welcomeMessage = new JSONObject();
            welcomeMessage.put("type", "chat");
            welcomeMessage.put("playerId", player.getId());
            welcomeMessage.put("roomId", player.getRoom());
            welcomeMessage.put("content", player.getName() + " has entered the chat room");
            
            // Broadcast message to all clients
            broadcastMessage(welcomeMessage.toString());

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
                    broadcastMessage(message.toString());
                } else {
                    break;
                }
            }
            
            // User is leaving the room
            broadcastMessage(player.getName() + " is leaving the room");
            
            // Remove this thread from threads
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this ) {
                    threads[i] = null;
                }
            }
            
            is.close();
            os.close();
            clientSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }
}