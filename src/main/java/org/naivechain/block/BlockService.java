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

    private String calculateHash(String previousHash, long timestamp, String data) {
        return CryptoUtil.getSHA256(previousHash + timestamp + data);
    }

    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    private Block getFirstBlock() {
        return new Block(1, System.currentTimeMillis(), "Hello Block", "aa212344fc10ea0a2cb885078fa9bc2354e55efc81be8f56b66e4a837157662e", "0");
    }

    public Block generateNextBlock(String blockData) {
        Block previousBlock = this.getLatestBlock();
        int nextIndex = previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis();
        String nextHash = calculateHash(previousBlock.getHash(), nextTimestamp, blockData);
        return new Block(nextIndex, nextTimestamp, blockData, nextHash, previousBlock.getHash());
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
            String hash = calculateHash(newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData());
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

    public List<Block> getBlockChain() {
        return blockChain;
    }
}
