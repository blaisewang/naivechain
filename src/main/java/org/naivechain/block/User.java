package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * naivechain
 * Created by blaisewang on 02/01/2018.
 */
public class User {
    @JSONField(ordinal = 1)
    private int node;
    @JSONField(ordinal = 2)
    private int address;


    User() {
        this.node = 0;
        this.address = 0;
    }

    User(int node, int address) {
        this.node = node;
        this.address = address;
    }

    User(String user) {
        String[] strings = user.split(".");
        this.node = Integer.parseInt(strings[0]);
        this.address = Integer.parseInt(strings[1]);
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean equals(User user) {
        return node == user.getNode() && address == user.getAddress();
    }

    @Override
    public String toString() {
        return node + "." + address;
    }
}
