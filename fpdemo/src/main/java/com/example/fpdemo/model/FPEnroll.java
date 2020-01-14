package com.example.fpdemo.model;

public class FPEnroll {
    int status;
    String fpTemplate = new String();

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFpTemplate(String fpTemplate) {
        this.fpTemplate = fpTemplate;
    }

    public int getStatus() {
        return status;
    }

    public String getFpTemplate() {
        return fpTemplate;
    }
}
