package gomoku;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Husni
 */
public class GomukuClient implements Runnable {
    public static Socket socket = null;
    public static DataInputStream is = null;
    public static PrintStream os = null;
    public static DataInputStream input = null;
    public static Board board;
    public static Player player;
    
    public static boolean closed = false;
    public static boolean start = false;
    
    public static void main(String args[]) {
        String host = "localhost";
        int port = 8000;
        board = new Board();
        
        // Get name and room id
        Scanner reader = new Scanner(System.in);
        System.out.println("Insert your name: ");
        String name = reader.nextLine();

        System.out.println("Insert room id: ");
        int roomId = reader.nextInt();

        player = new Player(name, roomId);
        
        // Create socket
        try {
            socket = new Socket(host, port);
            is = new DataInputStream(socket.getInputStream());
            os = new PrintStream(socket.getOutputStream());
            input = new DataInputStream(new BufferedInputStream(System.in));
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        
        // Read and write data through socket
        try {
            // Create a new thread to receive data from server
            new Thread(new GomukuClient()).start();
            
            // Wait until start
            while(!start) {}
            
            // Send player object
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(player);
            
            // Send data through socket
            while (!closed) {
                int x = reader.nextInt();
                int y = reader.nextInt();
                
                JSONArray coordinate = new JSONArray();
                coordinate.add(x);
                coordinate.add(y);
                
                JSONObject message = new JSONObject();
                message.put("type", "coordinate");
                message.put("playerId", player.getId());
                message.put("content", coordinate);
                
                os.println(message.toString());
            }
            
            is.close();
            os.close();
            oos.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    // Thread to receive data from server
    public void run() {
        String responseLine;
        try {
            // Receive player id
            responseLine = is.readLine();
            System.out.println(responseLine);
            player.setId(Integer.parseInt(responseLine));
            
            start = true;
            
            while((responseLine = is.readLine()) != null && start) {
                JSONObject message = new JSONObject();
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(responseLine);
                    message = (JSONObject) obj;
                } catch (ParseException ex) {
                    System.err.println(ex);
                }
                
                String type = (String) message.get("type");
                if (type.equals("coordinate")) {
                    JSONArray coordinate = new JSONArray();
                    coordinate = (JSONArray) message.get("content");
                    int x = ((Long)coordinate.get(0)).intValue();
                    int y = ((Long)coordinate.get(1)).intValue();
                    int playerId = ((Long)message.get("playerId")).intValue();
                    
                    board.add(x, y, playerId);
                    board.print();
                } else {
                    System.out.println("Chat biasa");
                    System.out.println(message.get("content"));
                }
                
                if (responseLine.indexOf("bye.") != -1) {
                    break;
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
