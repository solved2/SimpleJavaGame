package org.kunp.map;

public class Location{
    int roomNumber, x, y, role;
    public Location(int role, int roomNumber, int x, int y){
        this.role = role;
        this.roomNumber = roomNumber;
        this.x = x;
        this.y = y;
    }

    public void setLocation(int role, int roomNumber, int x, int y){
        this.role = role;
        this.roomNumber = roomNumber;
        this.x = x;
        this.y = y;
    }

    public int getRole() { return role; }
    public int getRoomNumber(){
        return roomNumber;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}