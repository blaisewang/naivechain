package org.naivechain.block;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * naivechain
 * Created by blaisewang on 03/01/2018.
 */
public class Transaction {
    @JSONField(ordinal = 1)
    private User payer;
    @JSONField(ordinal = 2)
    private User payee;
    @JSONField(ordinal = 3)
    private int amount;

    Transaction() {
    }

    Transaction(User payer, User payee, int amount) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }

    public User getPayer() {
        return payer;
    }

    public User getPayee() {
        return payee;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return payer.toString() + "#" + payee.toString() + "#" + amount;
    }
}
