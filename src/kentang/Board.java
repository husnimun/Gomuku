package kentang;

/**
 *
 * @author Husni
 */
public class Board {
    private int boardWidth = 20;
    private int boardHeight = 20;
    private int[][] board = new int[boardWidth][boardHeight];
    private static int dx[] = {-1, -1, 0, 1, -1, 0, 1, 1};
    private static int dy[] = {0, 1, 1, 1, -1, -1, -1, 0};
    
    public Board() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                board[i][j] = -1;
            }
        }
    }
    
    /**
     * 
     * @param x coordinate-x
     * @param y coordinate-y
     * @param el player
     * @return true if 'el' winning when put token
     * false otherwise
     */
    public boolean add(int x, int y, int el) {
        if (isEmpty(x, y)) {
            board[x][y] = el;
        } else {
            // do some action when board revisited
        }
        return isWin(x, y, el);
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
    
    private boolean isWin(int x, int y, int el) {
        assert board[x][y] == el;
        int[] isi = new int[8];
        for(int i = 0; i < 8; i++) {
          int tx = x + dx[i];
          int ty = y + dy[i];
          while(isValid(tx, ty)) {
              if(board[tx][ty] != el) break;
              isi[i]++;
              tx += dx[i];
              ty += dy[i];
          }
        }
        for(int i = 0; i < 8; i++) {
            if(isi[i] + isi[7 - i] >= 4) {
                return true; 
            }
        }
        return false;
    }
    
    private boolean isValid(int x, int y) {
        return 0 <= x && x < boardWidth && 0 <= y && y < boardHeight;
    }
}
