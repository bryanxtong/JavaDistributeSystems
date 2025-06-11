package com.example.dubbo;

public class HelloReply implements java.io.Serializable{
    private String message;

    public HelloReply() {
    }

    public HelloReply(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
