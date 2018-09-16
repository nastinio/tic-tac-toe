package ru.nastinio;

import javax.swing.*;

public class ButtonTicTac extends JButton {

    private int y;
    private int x;

    ButtonTicTac(char symbol,int y,int x){
        super.setText(String.valueOf(symbol));
        this.y=y;
        this.x=x;
    }

    public int getYCoordinate(){
        return y;
    }

    public int getXCoordinate(){
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }
}
