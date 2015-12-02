package kentang;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * All the game properties in GomokuServerThread is stored GomokuServer
 * This is because multi-threading static objects
 * @author Husni
 */
public class GomokuServer {
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static GameComponent a = new GameComponent();
    
    public static void main(String args[]) {
        int port = 8000;
        
        // Create a server socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e);
        }
        
        // Create a client socket for each connection and pass it to new thread
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < a.maxClientsCount; i++) {
                    if (a.threads[i] == null) {
                        (a.threads[i] = new GomukuServerThread(clientSocket, a)).start();
                        break;
                    }
                }
                
                if (i == a.maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}
