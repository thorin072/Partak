package com.delaquess.doodlz;

public class PointDobot{

    protected String point;
    private int x;
    private int y;

    public PointDobot(int x, int y) {

        this.x = y;
        this.y = y;
        this.point = new String();
        this.point= ("{\"x\":"+x+",\"y\":"+y+"}");
    }

    public void newPoint(int x, int y){

        this.x = x;
        this.y = y;
        this.point = new String();
        this.point= ("{\"x\":"+x+",\"y\":"+y+"}");
    }

    public int getX(){return this.x;}

    public int getY(){return this.y;}

    public String getPoint (){return this.point;}
}