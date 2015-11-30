    package gomoku;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Husni
 */
public class GomokuServer {
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static int maxClientsCount = 3;
    private static GomukuClientThread[] threads = new GomukuClientThread[maxClientsCount];
    
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
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new GomukuClientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                
                if (i == maxClientsCount) {
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
