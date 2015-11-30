package gomoku;

import java.io.Serializable;

/**
 *
 * @author Husni
 */
public class Player implements Serializable {
    private String name;
    private int room;
    private int id;
    
    // Constructor
    public Player(String name, int room) {
        this.name = name;
        this.room = room;
        this.id = 0;
    }
    
    // Setter and getter for name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    // Setter and getter for room
    public int getRoom() {
        return room;
    }
    public void setRoom(int room) {
        this.room = room;
    }
    
    // Stter and getter for id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
