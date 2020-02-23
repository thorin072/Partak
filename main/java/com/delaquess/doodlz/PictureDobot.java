package com.delaquess.doodlz;

import com.google.gson.annotations.SerializedName;

public class PictureDobot{

    @SerializedName("found_lines")
    private String lines;
    @SerializedName("found_count")
    private int lineId;

    public PictureDobot() {

        this.lines = new String();
        this.lineId = -1;
    }

    public void addLine(LineDobot line){

        this.lineId +=1;

        if (lineId==0){this.lines=("{\""+lineId+"\":["+line.getLine()+"]}");}
        else {this.lines=this.lines+(",{\""+lineId+"\":["+line.getLine()+"]}");}
    }

    public String getPicture(){return this.lines;}

    public int getLineId(){return this.lineId;}
}
