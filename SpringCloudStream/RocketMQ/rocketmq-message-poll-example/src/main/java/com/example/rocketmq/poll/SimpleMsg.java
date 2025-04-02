package com.example.rocketmq.poll;

/**
 * @author sorie
 */
public class SimpleMsg {

    private String msg;

    public SimpleMsg() {
    }

    public SimpleMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SimpleMsg{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
