/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kentang;

/**
 *
 * @author Luqman A. Siswanto
 */
public class GameComponent {
    public static final int maxClientsCount = 10;
    public static final int maxRoomCount = 5;
    
    public static GomukuServerThread[] threads = new GomukuServerThread[maxClientsCount];
    public static int[] playerNow = new int[maxRoomCount];
    public static int[] playerCount = new int[maxRoomCount];
    public static boolean[] isPlaying = new boolean[maxRoomCount];
    public static Board[] boards = new Board[maxRoomCount];
    // all player
    public static Player[] player = new Player[maxClientsCount];
    // players on spesific room, only when they'are playing
    public static int[][] players = new int[maxRoomCount][maxClientsCount];
    public static int roomCount = 0;
    
    public GameComponent() {
        for(int i = 0; i < maxClientsCount; i++) {
            threads[i] = null;
        }
        for(int i = 0; i < maxRoomCount; i++) {
            playerNow[i] = 0;
            playerCount[i] = 0;
            isPlaying[i] = false;
            boards[i] = new Board();
        }
    }
}
