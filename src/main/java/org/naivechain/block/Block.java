package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by sunysen on 2017/7/6.
 */
public class Block {
    @JSONField(ordinal = 1)
    private int index;
    @JSONField(ordinal = 2)
    private long timestamp;
    @JSONField(ordinal = 3)
    private String data;
    @JSONField(ordinal = 4)
    private String hash;
    @JSONField(ordinal = 5)
    private String previousHash;

    public Block() {
    }

    Block(int index, long timestamp, String data, String hash, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.hash = hash;
        this.previousHash = previousHash;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

