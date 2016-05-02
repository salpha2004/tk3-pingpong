package com.example.tk3.ponggame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.KeyEvent;

import org.umundo.core.Message;
import org.umundo.core.Publisher;

/**
 * Created by Mohit on 26.04.2016.
 */
public class GameState {

    Publisher pub;
    //screen width and height
    final int _screenWidth;
    final int _screenHeight;
    final float _scale;
    final int _numPlayers;
    //The ball
    final int _ballSize;
    final int _batSpeed = 3;

    int _ballX;
    int _ballY;
    int _ballVelocityX;
    int _ballVelocityY;

    //The bats
    final int _batLength;
    final int _batHeight;

    int _topBatX = 0; int _topBatY = 0;
    int _bottomBatX = 0; int _bottomBatY = 0;
    int _leftBatX = 0; int _leftBatY = 0;
    int _rightBatX = 0; int _rightBatY = 0;



    public GameState(int width, int height, float scale, int numPlayers) {
        _screenWidth = width;
        _screenHeight = height;
        _scale = scale;
        _numPlayers = numPlayers;

        _ballSize = dpToPx(12.5f);
        _ballX = (_screenWidth/2) - (_ballSize/2);
        _ballY = (_screenHeight/2) - (_ballSize/2);
        _ballVelocityX = dpToPx(1);
        _ballVelocityY = dpToPx(1);

        _batLength = dpToPx(100);
        _batHeight = dpToPx(3);

        _topBatX = (_screenWidth/2) - (_batLength / 2);
        _topBatY = 0;

        _bottomBatX = (_screenWidth/2) - (_batLength / 2);
        _bottomBatY = _screenHeight - _batHeight;

        _leftBatX = 0;
        _leftBatY = (_screenHeight/2) - (_batLength / 2);

        _rightBatX = _screenWidth - _batHeight;
        _leftBatY = (_screenHeight/2) - (_batLength / 2);

        this.pub = Mundo.getInstance().getPub();
    }

    private int dpToPx(float dp) {
        return (int)(dp * _scale);
    }
    //The update method
    public void update() {
        if (Mundo.getInstance().getId() == 0) {
            _ballX += _ballVelocityX;
            _ballY += _ballVelocityY;
            Message m = new Message();
            m.putMeta("ballX", "" + (float) _ballX / _screenWidth);
            m.putMeta("ballY", "" + (float) _ballY / _screenHeight);
            pub.send(m);
        }
        switch (_numPlayers) {
            case 4:
                if (collisionTopBat() || collisionBottomBat()) {
                    speedupBall();
                    _ballVelocityY *= -1;
                } else if (collisionLeftBat() || collisionRightBat()) {
                    speedupBall();
                    _ballVelocityX *= -1;
                } else if (_ballY < _topBatY) {
                    //Top-Player loses life
                    respawnBall();
                } else if (_ballY + _ballSize > _bottomBatY + _batHeight) {
                    //Bottom-Player loses life
                    respawnBall();
                } else if (_ballX < _leftBatX) {
                    //Left-Player loses life
                    respawnBall();
                } else if (_ballX + _ballSize > _rightBatX + _batHeight) {
                    //Right-Player loses life
                    respawnBall();
                }
                break;
            case 3:
                if (collisionTopBat() || collisionBottomBat()) {
                    speedupBall();
                    _ballVelocityY *= -1;
                } else if (collisionLeftBat()) {
                    speedupBall();
                    _ballVelocityX *= -1;
                } else if (collisionRightSide()) {
                    _ballVelocityX *= -1;
                } else if (_ballY < _topBatY) {
                    //Top-Player loses life
                    respawnBall();
                } else if (_ballY + _ballSize > _bottomBatY + _batHeight) {
                    //Bottom-Player loses life
                    respawnBall();
                } else if (_ballX < _leftBatX) {
                    //Left-Player loses life
                    respawnBall();
                }
                break;
            case 2:
                if (collisionTopBat() || collisionBottomBat()) {
                    speedupBall();
                    _ballVelocityY *= -1;
                } else if (collisionLeftSide() || collisionRightSide()) {
                    _ballVelocityX *= -1;
                } else if (_ballY < _topBatY) {
                    //Top-Player loses life
                    respawnBall();
                } else if (_ballY + _ballSize > _bottomBatY + _batHeight) {
                    //Bottom-Player loses life
                    respawnBall();
                }
                break;
            case 1:
                if (collisionBottomBat()) {
                    speedupBall();
                    _ballVelocityY *= -1;
                } else if (collisionLeftSide() || collisionRightSide()) {
                    _ballVelocityX *= -1;
                } else if (collisionTopSide()) {
                    _ballVelocityY *= -1;
                } else if (_ballY + _ballSize > _bottomBatY + _batHeight) {
                    //Bottom-Player loses life
                    respawnBall();
                }
                break;
        }

    }

    private boolean collisionLeftSide() {
        return (_ballX < 0);
    }
    private boolean collisionRightSide() {
        return (_ballX + _ballSize > _screenWidth);
    }
    private boolean collisionTopSide() {
        return  (_ballY < 0);
    }
    private boolean collisionTopBat() {
        return (_ballX > _topBatX && _ballX < _topBatX + _batLength && _ballY < _topBatY);
    }
    private boolean collisionBottomBat() {
        return (_ballX > _bottomBatX && _ballX < _bottomBatX + _batLength && _ballY + _ballSize > _bottomBatY);
    }
    private boolean collisionLeftBat() {
        return (_ballY > _leftBatY && _ballY < _leftBatY + _batLength && _ballX < _leftBatX);
    }
    private boolean collisionRightBat() {
        return (_ballY > _rightBatY && _ballY < _rightBatY + _batLength && _ballX + _ballSize > _rightBatX);
    }

    private void respawnBall() {
        _ballVelocityX = dpToPx(1);
        _ballVelocityY = dpToPx(1);
        _ballX = (_screenWidth/2) - (_ballSize/2);
        _ballY = (_screenHeight/2) - (_ballSize/2);
    }

    private void speedupBall() {
        float speedup = 0.5f;
        _ballVelocityX += (_ballVelocityX < 0) ? -dpToPx(speedup) : dpToPx(speedup);
        _ballVelocityY += (_ballVelocityY < 0) ? -dpToPx(speedup) : dpToPx(speedup);
    }
    public boolean keyPressed(int keyCode, KeyEvent msg)
    {
        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) //left
        {
            _topBatX += _batSpeed; _bottomBatX -= _batSpeed;
        }

        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) //right
        {
            _topBatX -= _batSpeed; _bottomBatX += _batSpeed;
        }

        return true;
    }

    public boolean moveBat(float d) {
        int id = Mundo.getInstance().getId();
        float newpos = 0;
        switch (id) {
            case 0:
                _bottomBatX += d;
                if (_bottomBatX < 0) _bottomBatX = 0;
                if (_bottomBatX+_batLength > _screenWidth) _bottomBatX = _screenWidth-_batLength;
                newpos = (float)_bottomBatX/_screenWidth;
                break;
            case 1:
                _topBatX += d;
                if (_topBatX < 0) _topBatX = 0;
                if (_topBatX+_batLength > _screenWidth) _topBatX = _screenWidth-_batLength;
                newpos = (float)_topBatX/_screenWidth;
                break;
            case 2:
                _leftBatY += d;
                if (_leftBatY < 0) _leftBatY = 0;
                if (_leftBatY+_batLength > _screenHeight) _leftBatY = _screenHeight-_batLength;
                newpos = (float)_leftBatY/_screenHeight;
                break;
            case 3:
                _rightBatY += d;
                if (_rightBatY < 0) _rightBatY = 0;
                if (_rightBatY+_batLength > _screenHeight) _rightBatY = _screenHeight-_batLength;
                newpos = (float)_rightBatY/_screenHeight;
                break;
        }

        //byte[] b = java.nio.ByteBuffer.allocate(4).putInt(_bottomBatX).array();
        //pub.send(b);
        Message m = new Message();
        m.putMeta("id", String.valueOf(id));
        m.putMeta("pos", String.valueOf(newpos));
        pub.send(m);
        return true;
    }

    public void moveOpponentBat(int id, float x) {
        switch (id) {
            case 0: _bottomBatX = (int)(x*(float)_screenWidth); break;
            case 1: _topBatX = (int)(x*(float)_screenWidth); break;
            case 2: _leftBatY = (int)(x*(float)_screenHeight); break;
            case 3: _rightBatY = (int)(x*(float)_screenHeight); break;
        }

    }

    //the draw method
    public void draw(Canvas canvas, Paint paint) {

//Clear the screen
        canvas.drawRGB(20, 20, 20);

//set the colour
        //paint.setARGB(200, 0, 200, 0);

//draw the ball
        paint.setColor(Color.WHITE);
        canvas.drawRect(new Rect(_ballX,_ballY,_ballX + _ballSize,_ballY + _ballSize),
                paint);

//draw the bats
        switch (_numPlayers) {
            case 4:
                paint.setARGB(200, 200, 200, 0);
                canvas.drawRect(new Rect(_rightBatX, _rightBatY, _rightBatX+_batHeight, _rightBatY+_batLength), paint);
            case 3:
                paint.setARGB(200, 0, 200, 0);
                canvas.drawRect(new Rect(_leftBatX, _leftBatY, _leftBatX+_batHeight, _leftBatY+_batLength), paint);
            case 2:
                paint.setARGB(200, 200, 0, 0);
                canvas.drawRect(new Rect(_topBatX, _topBatY, _topBatX + _batLength, _topBatY + _batHeight), paint);
            case 1:
                paint.setARGB(200, 0, 0, 200);
                canvas.drawRect(new Rect(_bottomBatX, _bottomBatY, _bottomBatX + _batLength, _bottomBatY + _batHeight), paint);
        }

    }

    public void setBall(float x, float y) {
        _ballX = (int)(x*(float)_screenWidth);
        _ballY = (int)(y*(float)_screenHeight);
    }
}