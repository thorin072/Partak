package com.delaquess.doodlz;

public class PacketDobot{

    private String packet;

    public PacketDobot(String pictureName,PictureDobot picture){

        this.packet = new String();
        this.packet=("{\""+pictureName+"\":["+picture.getPicture()+"]}");
    }

    public String getPacket(){return this.packet;}
}
