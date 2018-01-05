package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * naivechain
 * Created by blaisewang on 03/01/2018.
 */
public class Transaction {
    @JSONField(ordinal = 1)
    private int index;
    @JSONField(ordinal = 2)
    private String payer;
    @JSONField(ordinal = 3)
    private String payee;
    @JSONField(ordinal = 4)
    private int amount;
    @JSONField(ordinal = 4)
    private boolean isIgnore;

    Transaction() {
    }

    Transaction(int index, User payer, User payee, int amount, boolean isIgnore) {
        this.index = index;
        this.payer = payer.toString();
        this.payee = payee.toString();
        this.amount = amount;
        this.isIgnore = isIgnore;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public void setIgnore(boolean isIgnore) {
        this.isIgnore = isIgnore;
    }

    @Override
    public String toString() {
        return "{" + payer + ", " + payee + ", " + amount + "}";
    }
}
