package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * naivechain
 * Created by blaisewang on 03/01/2018.
 */
public class Transaction {
    @JSONField(ordinal = 1)
    private String payer;
    @JSONField(ordinal = 2)
    private String payee;
    @JSONField(ordinal = 3)
    private int amount;

    Transaction() {
    }

    Transaction(String payer, String payee, int amount) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }

    public String getPayer() {
        return payer;
    }

    public String getPayee() {
        return payee;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "{" + payer + ", " + payee + ", " + amount + "}";
    }
}
