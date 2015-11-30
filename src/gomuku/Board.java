package gomuku;

/**
 *
 * @author Husni
 */
public class Board {
    private int boardWidth = 20;
    private int boardHeight = 20;
    private int[][] board = new int[boardWidth][boardHeight];
    
    public Board() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                board[i][j] = -1;
            }
        }
    }
    
    public void add(int x, int y, int el) {
        if (isEmpty(x, y)) {
            board[x][y] = el;
        }
    }
    
    public void print() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                System.out.print(board[i][j]);
                System.out.print("  ");
            }
            System.out.println("");
        }
    }
    
    public boolean isEmpty(int x, int y) {
        return board[x][y] == -1;
    }
}
