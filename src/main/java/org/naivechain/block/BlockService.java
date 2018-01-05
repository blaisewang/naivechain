package org.naivechain.block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunysen on 2017/7/6.
 */
public class BlockService {
    private int mainHost;
    private List<Block> blockChain;
    private List<Transaction> transactions;
    private List<List<Block>> blockchainSnapshots;

    BlockService(int mainHost) {
        this.mainHost = mainHost;
        blockChain = new ArrayList<>();
        transactions = new ArrayList<>();
        blockchainSnapshots = new ArrayList<>();
        transactions.add(new Transaction(0, new User(), new User(), 0, false));
        blockChain.add(getFirstBlock());
    }

    private String calculateHash(int index, long timestamp, String transactions, String previousHash) {
        return CryptoUtil.getSHA256(index + timestamp + transactions + previousHash);
    }

    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    private Block getFirstBlock() {
        int index = 0;
        long timestamp = 1514936633890L;
        List<String> blockTransactions = new ArrayList<>();
        blockTransactions.add(new Transaction(0, new User(), new User(mainHost, 0), 16, false).toString());
        String previousHash = "0";
        String hash = calculateHash(index, timestamp, blockTransactions.toString(), previousHash);
        return new Block(index, timestamp, blockTransactions, hash, previousHash);
    }

    public Block generateNextBlock(List<String> blockTransactions) {
        Block previousBlock = getLatestBlock();
        int nextIndex = previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis();
        String nextHash = calculateHash(nextIndex, nextTimestamp, blockTransactions.toString(), previousBlock.getHash());
        return new Block(nextIndex, nextTimestamp, blockTransactions, nextHash, previousBlock.getHash());
    }

    public void addBlock(Block newBlock) {
        if (isValidNewBlock(newBlock, getLatestBlock())) {
            blockChain.add(newBlock);
        }
    }

    private boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
            System.out.println("Invalid index");
            return false;
        } else if (!previousBlock.getHash().equals(newBlock.getPreviousHash())) {
            System.out.println("Invalid previous hash");
            return false;
        } else {
            String hash = calculateHash(newBlock.getIndex(), newBlock.getTimestamp(), newBlock.getTransactions().toString(), newBlock.getPreviousHash());
            if (!hash.equals(newBlock.getHash())) {
                System.out.println("Invalid hash: " + hash + " " + newBlock.getHash());
                return false;
            }
        }
        return true;
    }

    public void replaceChain(List<Block> newBlocks) {
        if (isValidBlocks(newBlocks) && newBlocks.size() > blockChain.size()) {
            for (int i = 0; i < blockChain.size(); i++) {
                if (blockChain.get(i) != newBlocks.get(i)) {
                    blockchainSnapshots.add(blockChain.subList(i, blockChain.size() - 1));
                    break;
                }
            }
            blockChain = newBlocks;
        } else {
            System.out.println("Received an invalid blockchain");
        }
    }

    private boolean isValidBlocks(List<Block> newBlocks) {
        Block firstBlock = newBlocks.get(0);
        if (firstBlock.equals(getFirstBlock())) {
            return false;
        }

        for (int i = 1; i < newBlocks.size(); i++) {
            if (isValidNewBlock(newBlocks.get(i), firstBlock)) {
                firstBlock = newBlocks.get(i);
            } else {
                return false;
            }
        }
        return true;
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public List<List<Block>> getBlockchainSnapshots() {
        return blockchainSnapshots;
    }

    public int getTransactionSize() {
        return transactions.size();
    }

    public Transaction getLatestTransaction() {
        return transactions.get(transactions.size() - 1);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void replaceTransactions(List<Transaction> newTransactions) {
        if (newTransactions.size() > transactions.size()) {
            transactions = newTransactions;
        }
    }

    public void removeTransaction(int index) {
        transactions.remove(index);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
