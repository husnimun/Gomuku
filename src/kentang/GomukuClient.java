package kentang;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    public static Board board;
    public static Player player;
    
    public static boolean closed = false;
    public static boolean start = false;
    
    public static boolean canPlay = false;
    public static boolean isPlaying = false;
    
    /********************** HANDLE REQUEST TO SERVER **********************/
    
    public static void createRoom() {
        if(player.getRoom() != 0) {
            System.out.println("You cannot create room. Please exit current room.");
            return;
        }
        JSONObject message = new JSONObject();
        message.put("type", "create");
        message.put("playerId", player.getId());
        os.println(message.toString());
    }
    
    public static void joinRoom(int roomId) {
        if(player.getRoom() != 0) {
            System.out.println("You cannot join room. Please exit current room.");
            return;
        }
        JSONObject message = new JSONObject();
        message.put("type", "join");
        message.put("playerId", player.getId());
        message.put("roomId", roomId);
        os.println(message.toString());
    }
    
    public static void exitRoom() {
        if(player.getRoom() == 0) {
            System.out.println("You are not belong to any spesific room.");
            return;
        }
        JSONObject message = new JSONObject();
        message.put("type", "exit");
        message.put("playerId", player.getId());
        os.println(message.toString());
    }
    
    public static void clickPlay() {
        if(player.getRoom() == 0) {
            System.out.println("You cannot play. Enter the room first.");
            return;
        }
        if(!canPlay) {
            System.out.println("You cannot play. Minimum player in the room should be 3.");
            return;
        }
        JSONObject message = new JSONObject();
        message.put("type", "play");
        message.put("playerId", player.getId());
        os.println(message.toString());
    }
    
    public static void sendChat(String message) {
        JSONObject object = new JSONObject();
        object.put("type", "chat");
        object.put("playerId", player.getId());
        object.put("roomId", player.getRoom());
        object.put("content", message);
        os.println(object.toString());
    }
    
    public static void sendCoordinate(int x, int y) {
        JSONArray coordinate = new JSONArray();
        coordinate.add(x);
        coordinate.add(y);
        
        JSONObject object = new JSONObject();
        object.put("type", "coordinate");
        object.put("playerId", player.getId());
        object.put("roomId", player.getRoom());
        object.put("content", coordinate);
        os.println(object.toString());
    }
    
    /********************** HANDLE RESPONSE FROM SERVER **********************/
    
    public static void chat(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = ((Long) object.get("roomId")).intValue();
        String name = (String) object.get("name");
        String content = (String) object.get("content");
        System.out.format("> %s : %s\n", name, content);
    }
    
    public static void addCoordinate(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = ((Long) object.get("roomId")).intValue();
        String name = (String) object.get("name");
        JSONArray coordinate = (JSONArray) object.get("content");
        int i = ((Long)coordinate.get(0)).intValue();
        int j = ((Long)coordinate.get(1)).intValue();
        board.add(i, j, playerId);
        board.print();
        System.out.format("%s taking turn at (%d, %d)\n", name, i, j);
    }
    
    public static void joinRoom(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        int roomId = ((Long) object.get("roomId")).intValue();
        if(roomId == 0) {
            System.out.format("You have exited from room %d\n", player.getRoom());
        } else {
            System.out.format("You entered room %d\n", roomId);
        }
        player.setRoom(roomId);
    }
    
    public static void printHomeStatus(JSONObject object) {
        JSONArray rooms = (JSONArray) object.get("content");
        int size = rooms.size();
        if(size == 0) {
            System.out.println("There is no room active");
            return;
        } 
        System.out.format("%7s | %17s | %9s\n", "Room ID", "Number of Players", "isPlaying");
        for(int i = 0; i < size; i++) {
            JSONObject room = (JSONObject) rooms.get(i);
            int roomId = ((Long) room.get("roomId")).intValue();
            int playerCount = ((Long) room.get("playerCount")).intValue();
            boolean isPlaying = (boolean) room.get("isPlaying");
            System.out.format("%7s | %17s | %9s\n", roomId, playerCount, isPlaying);
        }
        System.out.println();
    }
    
    public static void printRoomStatus(JSONObject object) {
        JSONArray players = (JSONArray) object.get("content");
        int size = players.size();
        if(size == 0) {
            System.out.println("There is no player in this room yet.");
            return;
        } 
        System.out.format("%2s | Name\n", "ID");
        for(int i = 0; i < size; i++) {
            JSONObject room = (JSONObject) players.get(i);
            int playerId = ((Long) room.get("playerId")).intValue();
            String name = (String) room.get("name");
            System.out.format("%2s | %15s\n", playerId, name);
        }
        System.out.println();
    }
    
    public static void canPlay(JSONObject object) {
        canPlay = true;
        System.out.println("TYPE \"play\" to START THE GAME!");
    }
    
    public static void disablePlay(JSONObject object) {
        canPlay = false;
        System.out.println("Cannot start the game. Waiting other player...");
    }
    
    public static void play(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        String name = (String) object.get("name");
        board = new Board();
        board.print();
        System.out.format("%s (ID = %d) start the game!\n", name, playerId);
        isPlaying = true;
    }
    
    public static void unplay(JSONObject object) {
        isPlaying = false;
        System.out.println("The game has been ended!");
    }
    
    public static void win(JSONObject object) {
        int playerId = ((Long) object.get("playerId")).intValue();
        String name = (String) object.get("name");
        if(playerId == player.getId()) {
            System.out.println("You won the game!");
        } else {
            System.out.format("You lost! %s win the game!\n", name);
        }
    }
    
    public static void not(JSONObject object) {
        System.out.println("It's not your turn.");
    }
    
    public static void turn(JSONObject object) {
        System.out.println("It's your turn!");
    }
    
    public static void playing(JSONObject object) {
        int roomId = ((Long) object.get("roomId")).intValue();
        System.out.format("You cannot enter room %d, they are playing.\n", roomId);
    }
    
    public static void main(String args[]) {
        String host = "localhost";
        int port = 8000;
        board = new Board();
        
        // Get host, port, and player name
        GUI.LandingPage landingPage = new GUI.LandingPage();
        landingPage.setVisible(true);
        
        while(landingPage.isShowing()) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        
        Scanner reader = new Scanner(System.in);
        
        player = new Player(landingPage.getName());
        System.out.println("Connecting to " + landingPage.getHost() + " port: " + landingPage.getPort());
        
        
        // Create socket
        try {
            socket = new Socket(host, port);
            is = new DataInputStream(socket.getInputStream());
            os = new PrintStream(socket.getOutputStream());
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
            while(!start) {
                Thread.sleep(500);
            }
            
            System.out.println("Sudah tidak start lagi");
            
            // Send player object
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(player);
            
            System.out.println("Your ID is " + player.getId());
            
            // Send data through socket
            while (!closed) {
                String console = reader.nextLine();
                String[] command = console.split(" ", 2);
                if(command[0].equals("create")) {
                    createRoom();
                } else if(command[0].equals("join")) {
                    joinRoom(Integer.parseInt(command[1]));
                } else if(command[0].equals("exit")) {
                    exitRoom();
                } else if(command[0].equals("play")) {
                    clickPlay();
                } else if(command[0].equals("message")) {
                    sendChat(command[1]);
                } else {    // sending coordinate
                    sendCoordinate(Integer.parseInt(command[0]), Integer.parseInt(command[1]));
                }
            }
            
            is.close();
            os.close();
            oos.close();
            socket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    /********************** THREAD TO RECEIVE DATA FROM SERVER **********************/
    public void run() {
        String responseLine;
        try {
            // Receive player id
            responseLine = is.readLine();
            player.setId(Integer.parseInt(responseLine));
            System.out.println(player.getId());
            
            start = true;
            System.out.println("start udah bener");
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
                if (type.equals("chat")) {
                    chat(message);
                } else if(type.equals("coordinate")) {
                    addCoordinate(message);
                } else if(type.equals("join")) {
                    joinRoom(message);
                } else if(type.equals("home")) {
                    printHomeStatus(message);
                } else if(type.equals("room")) {
                    printRoomStatus(message);
                } else if(type.equals("can")) {
                    canPlay(message);
                } else if(type.equals("disable")) {
                    disablePlay(message);
                } else if(type.equals("play")) {
                    play(message);
                } else if(type.equals("unplay")) {
                    unplay(message);
                } else if(type.equals("win")) {
                    win(message);
                } else if(type.equals("not")) {
                    not(message);
                } else if(type.equals("turn")) {
                    turn(message);
                } else if(type.equals("playing")) {
                    playing(message);
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
