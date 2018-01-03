package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by sunysen on 2017/7/6.
 */
public class Block {
    @JSONField(ordinal = 1)
    private int index;
    @JSONField(ordinal = 2)
    private long timestamp;
    @JSONField(ordinal = 3)
    private List<Transaction> transactions;
    @JSONField(ordinal = 4)
    private String hash;
    @JSONField(ordinal = 5)
    private String previousHash;


    Block() {
    }

    Block(int index, long timestamp, List<Transaction> transactions, String hash, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.hash = hash;
        this.previousHash = previousHash;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
}
