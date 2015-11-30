package gomoku;

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

class GomukuClientThread extends Thread {
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    
    private GomukuClientThread[] threads;
    private int maxClientsCount;

    public GomukuClientThread(Socket clientSocket, GomukuClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
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
            Player player = (Player) ois.readObject();
            
            // Send welcome message to connected User
            JSONObject welcomeMessage = new JSONObject();
            welcomeMessage.put("type", "chat");
            welcomeMessage.put("playerId", player.getId());
            welcomeMessage.put("content", "Welcome " + player.getName() + " to chat room");
            os.println(welcomeMessage.toString());
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    welcomeMessage.put("content", player.getName() + " has entered the chat room");
                    threads[i].os.println(welcomeMessage.toString());
                }
            }

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
                
                // Broadcast the message to another clients
                String type = (String) message.get("type");
                if (!type.equals("end")) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null) {
                            threads[i].os.println(message.toString());
                        }
                    }
                } else {
                    break;
                }
            }
            
            os.println("bye.");
//            // User is leaving the room
//            for (int i = 0; i < maxClientsCount; i++) {
//                if (threads[i] != null && threads[i] != this) {
//                    threads[i].os.println(player.getName() + " is leaving the room");
//                }
//            }

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