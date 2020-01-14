package com.example.fpdemo.model;

public class FPImage {
    int status;
    int msgType;
    int index;
    int width;
    int height;
    String image = new String();

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getIndex() {
        return index;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getImage() {
        return image;
    }
}
