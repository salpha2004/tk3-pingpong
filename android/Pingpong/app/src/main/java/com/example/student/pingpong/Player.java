package com.example.student.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.SensorManager;
import android.view.View;

/**
 * Created by student on 24.04.16.
 */
public class Player extends View {

    private ShapeDrawable mDrawable;
    private int width;
    private int height;

    public Player(Context context, int x, int y, int width, int height){
        super(context);

        this.width = width;
        this.height = height;
        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(x, y, x + width, y + height);
    }

    public void rePosition(Position position){
        int x = Math.round(position.getXPos());
        int y = Math.round(position.getYPos());
        mDrawable.setBounds(x, y, x + this.width, y + this.height);
    }

    protected void onDraw(Canvas canvas){
        mDrawable.draw(canvas);
    }

    public void update(){

    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        mDrawable.draw(canvas);

    }
}
