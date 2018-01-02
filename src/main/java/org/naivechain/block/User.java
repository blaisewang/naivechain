package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * naivechain
 * Created by blaisewang on 02/01/2018.
 */
public class User {
    @JSONField(ordinal = 1)
    private String peer;
    @JSONField(ordinal = 2)
    private int address;


    User(String peer, int address) {
        this.peer = peer;
        this.address = address;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int index) {
        this.address = index;
    }
}
