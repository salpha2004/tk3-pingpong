package com.example.student.pingpong;

/**
 * Created by student on 28.04.16.
 */
public class Position {
    private float xPos;
    private float yPos;

    public Position(float x, float y){
        xPos = x;
        yPos = y;
    }

    public float getXPos(){
        return this.xPos;
    }

    public float getYPos(){
        return this.yPos;
    }

    public Position newPos(float x, float y){
        return new Position(xPos + x, yPos + y);
    }

}
