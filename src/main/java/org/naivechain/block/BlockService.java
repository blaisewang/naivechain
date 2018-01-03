package org.naivechain.block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunysen on 2017/7/6.
 */
public class BlockService {
    private List<Block> blockChain;

    BlockService() {
        this.blockChain = new ArrayList<>();
        blockChain.add(this.getFirstBlock());
    }

    private String calculateHash(int index, long timestamp, String data, String previousHash) {
        return CryptoUtil.getSHA256(index + timestamp + data + previousHash);
    }

    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    private Block getFirstBlock() {
        int index = 0;
        long timestamp = 1514936633890L;
        String data = "genesis-block";
        String previousHash = "0";
        String hash = calculateHash(index, timestamp, data, previousHash);
        User owner = new User(3030, 0);
        return new Block(index, timestamp, data, hash, previousHash, owner);
    }

    public Block generateNextBlock(User owner) {
        Block previousBlock = this.getLatestBlock();
        int nextIndex = previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis();
        String data = "Some data";
        String nextHash = calculateHash(nextIndex, nextTimestamp, data, previousBlock.getHash());
        return new Block(nextIndex, nextTimestamp, data, nextHash, previousBlock.getHash(), owner);
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
            String hash = calculateHash(newBlock.getIndex(), newBlock.getTimestamp(), newBlock.getData(), newBlock.getPreviousHash());
            if (!hash.equals(newBlock.getHash())) {
                System.out.println("Invalid hash: " + hash + " " + newBlock.getHash());
                return false;
            }
        }
        return true;
    }

    public void replaceChain(List<Block> newBlocks) {
        if (isValidBlocks(newBlocks) && newBlocks.size() > blockChain.size()) {
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

    public String getMoneyHash(User user) {
        for (Block block : blockChain) {
            if (block.getOwner().equals(user)) {
                return block.getHash();
            }
        }
        return "0";
    }

    public void setMoneyOwner(User user, String hash) {
        for (Block block : blockChain) {
            if (block.getHash().equals(hash)) {
                block.setOwner(user);
                return;
            }
        }
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }
}
