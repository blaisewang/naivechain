package org.naivechain.block;

import com.alibaba.fastjson.JSON;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sunysen on 2017/7/6.
 */
public class P2PService {
    private List<WebSocket> sockets;
    private BlockService blockService;
    private final static int QUERY_LATEST_BLOCKCHAIN = 0;
    private final static int QUERY_ALL_BLOCKCHAIN = 1;
    private final static int RESPONSE_BLOCKCHAIN = 2;
    private final static int QUERY_LATEST_TRANSACTION = 3;
    private final static int QUERY_ALL_TRANSACTION = 4;
    private final static int RESPONSE_TRANSACTION = 5;

    P2PService(BlockService blockService) {
        this.blockService = blockService;
        this.sockets = new ArrayList<>();
    }

    public void initP2PServer(int port) {
        final WebSocketServer socket = new WebSocketServer(new InetSocketAddress(port)) {
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                write(webSocket, queryBlockchainLengthMsg());
                write(webSocket, queryTransactionLengthMsg());
                sockets.add(webSocket);
            }

            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                System.out.println("Connection failed to peer:" + webSocket.getRemoteSocketAddress());
                sockets.remove(webSocket);
            }

            public void onMessage(WebSocket webSocket, String s) {
                handleMessage(webSocket, s);
            }

            public void onError(WebSocket webSocket, Exception e) {
                System.out.println("Connection failed to peer:" + webSocket.getRemoteSocketAddress());
                sockets.remove(webSocket);
            }

            public void onStart() {

            }
        };
        socket.start();
        System.out.println("Listening WebSocket P2P Port on: " + port);
    }

    private void handleMessage(WebSocket webSocket, String string) {
        try {
            Message message = JSON.parseObject(string, Message.class);
            System.out.println("Received message" + JSON.toJSONString(message, true));
            switch (message.getType()) {
                case QUERY_LATEST_BLOCKCHAIN:
                    write(webSocket, responseLatestBlockchainMsg());
                    break;
                case QUERY_ALL_BLOCKCHAIN:
                    write(webSocket, responseBlockchainMsg());
                    break;
                case RESPONSE_BLOCKCHAIN:
                    handleBlockchainResponse(message.getData());
                    break;
                case QUERY_LATEST_TRANSACTION:
                    write(webSocket, responseLatestTransactionMsg());
                    break;
                case QUERY_ALL_TRANSACTION:
                    write(webSocket, responseTransactionMsg());
                    break;
                case RESPONSE_TRANSACTION:
                    handleTransactionResponse(message.getData());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBlockchainResponse(String message) {
        List<Block> receivedBlocks = JSON.parseArray(message, Block.class);
        receivedBlocks.sort(Comparator.comparingInt(Block::getIndex));

        Block latestBlockReceived = receivedBlocks.get(receivedBlocks.size() - 1);
        Block latestBlock = blockService.getLatestBlock();
        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
            if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
                System.out.println("We can append the received block to our chain");
                blockService.addBlock(latestBlockReceived);
                broadcast(responseLatestBlockchainMsg());
            } else if (receivedBlocks.size() == 1) {
                System.out.println("We have to query the chain from our peer");
                broadcast(queryAllBlockchainMsg());
            } else {
                blockService.replaceChain(receivedBlocks);
            }
        } else {
            System.out.println("Received blockchain is not longer than current blockchain. Do nothing");
        }
    }

    private void handleTransactionResponse(String message) {
        List<Transaction> receivedTransactions = JSON.parseArray(message, Transaction.class);
        receivedTransactions.sort(Comparator.comparingLong(Transaction::getIndex));

        Transaction latestTransactionReceived = receivedTransactions.get(receivedTransactions.size() - 1);
        Transaction latestTransaction = blockService.getLatestTransaction();
        if (latestTransactionReceived.getIndex() > latestTransaction.getIndex()) {
            if (latestTransaction.getIndex() == latestTransactionReceived.getIndex() - 1) {
                blockService.addTransaction(latestTransactionReceived);
                broadcast(responseLatestTransactionMsg());
            } else if (receivedTransactions.size() == 1) {
                broadcast(queryAllTransactionMsg());
            } else {
                blockService.replaceTransactions(receivedTransactions);
            }
        }

    }

    public void connectToPeer(String peer) {
        try {
            final WebSocketClient socket = new WebSocketClient(new URI(peer)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    write(this, queryBlockchainLengthMsg());
                    write(this, queryTransactionLengthMsg());
                    sockets.add(this);
                }

                @Override
                public void onMessage(String s) {
                    handleMessage(this, s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("Connection failed");
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("Connection failed");
                    sockets.remove(this);
                }
            };
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void write(WebSocket webSocket, String message) {
        webSocket.send(message);
    }

    public void broadcast(String message) {
        for (WebSocket socket : sockets) {
            this.write(socket, message);
        }
    }

    private String queryAllBlockchainMsg() {
        return JSON.toJSONString(new Message(QUERY_ALL_BLOCKCHAIN));
    }

    private String queryBlockchainLengthMsg() {
        return JSON.toJSONString(new Message(QUERY_LATEST_BLOCKCHAIN));
    }

    private String responseBlockchainMsg() {
        return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(blockService.getBlockChain())));
    }

    public String responseLatestBlockchainMsg() {
        Block[] blocks = {blockService.getLatestBlock()};
        return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks)));
    }

    private String queryAllTransactionMsg() {
        return JSON.toJSONString(new Message(QUERY_ALL_TRANSACTION));
    }

    private String queryTransactionLengthMsg() {
        return JSON.toJSONString(new Message(QUERY_LATEST_TRANSACTION));
    }

    private String responseTransactionMsg() {
        return JSON.toJSONString(new Message(RESPONSE_TRANSACTION, JSON.toJSONString(blockService.getTransactions())));
    }

    public String responseLatestTransactionMsg() {
        Transaction[] transactions = {blockService.getLatestTransaction()};
        return JSON.toJSONString(new Message(RESPONSE_TRANSACTION, JSON.toJSONString(transactions)));
    }

    public List<WebSocket> getSockets() {
        return sockets;
    }
}
