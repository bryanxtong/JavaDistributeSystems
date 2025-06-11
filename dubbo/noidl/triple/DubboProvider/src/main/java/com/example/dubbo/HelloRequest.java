package com.example.dubbo;

public class HelloRequest implements java.io.Serializable{
    private String name;

    public HelloRequest() {
    }

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
