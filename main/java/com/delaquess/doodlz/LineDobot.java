package com.delaquess.doodlz;
import com.google.gson.annotations.SerializedName;

public class LineDobot{

    @SerializedName("found_lines")
    private String  points;

    @SerializedName("found_lines")
    private int pointId;

    public LineDobot() {

        this.points = new String();
        this.pointId = -1;

    }

    public void newLine() {

        this.points = new String();
        this.pointId = -1;

    }

    public void addPoint(PointDobot point) {

        this.pointId += 1;

        if (pointId == 0) {this.points = ("{\"" + pointId + "\":" + point.getPoint()+"}");}
        else {this.points = this.points+(",{\"" + pointId + "\":" + point.getPoint()+"}"); }

    }

    public String getLine(){return this.points;}

}

