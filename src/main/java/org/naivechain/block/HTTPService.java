package org.naivechain.block;

import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunysen on 2017/7/6.
 */
public class HTTPService {
    private int mainHost;
    private BlockService blockService;
    private UserService userService;
    private P2PService p2pService;
    private List<Transaction> transactions;


    HTTPService() {
    }

    HTTPService(int mainHost, BlockService blockService, UserService userService, P2PService p2pService) {
        this.mainHost = mainHost;
        this.blockService = blockService;
        this.userService = userService;
        this.p2pService = p2pService;
        transactions = new ArrayList<>();
    }

    public void initialHTTPServer(int port) {
        try {
            Server server = new Server(port);
            System.out.println("Listening HTTP Port on: " + port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            context.addServlet(new ServletHolder(new BlocksServlet()), "/blocks");
            context.addServlet(new ServletHolder(new MineBlockServlet()), "/mineBlock");
            context.addServlet(new ServletHolder(new PeersServlet()), "/peers");
            context.addServlet(new ServletHolder(new AddPeerServlet()), "/addPeer");
            context.addServlet(new ServletHolder(new UsersServlet()), "/users");
            context.addServlet(new ServletHolder(new AddUserServlet()), "/addUser");
            context.addServlet(new ServletHolder(new TransferServlet()), "/transfer");
            context.addServlet(new ServletHolder(new TransactionServlet()), "/transaction");
            context.addServlet(new ServletHolder(new QueryPayeeServlet()), "/queryPayee");
            context.addServlet(new ServletHolder(new AddTransactionServlet()), "/addTransaction");
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class BlocksServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(JSON.toJSONString(blockService.getBlockChain(), true));
        }
    }


    private class MineBlockServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            this.doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            User user = new User(req.getLocalPort(), Integer.parseInt(req.getParameter("miner")));
            if (userService.isValIdUser(user)) {
                int length = transactions.size();
                Map<String, Integer> map = new HashMap<>();
                List<String> blockTransactions = new ArrayList<>();

                map.put(user.toString(), getBalance(user) + 16);
                for (int i = 0; i < length; i++) {
                    Transaction transaction = transactions.get(0);

                    User payer = transaction.getPayer();
                    User payee = transaction.getPayee();
                    int payerBalance, payeeBalance;

                    if (map.containsKey(payer.toString())) {
                        payerBalance = map.get(payer.toString());
                    } else {
                        payerBalance = getBalance(payer);
                    }
                    if (map.containsKey(payee.toString())) {
                        payeeBalance = map.get(payee.toString());
                    } else {
                        payeeBalance = getBalance(payee);
                    }

                    if (payerBalance - transaction.getAmount() > 0) {
                        blockTransactions.add(transaction.toString());
                        payerBalance -= transaction.getAmount();
                        payeeBalance += transaction.getAmount();
                        map.put(transaction.getPayer().toString(), payerBalance);
                        map.put(transaction.getPayee().toString(), payeeBalance);
                    }
                    transactions.remove(0);
                    if (blockTransactions.size() == 3) {
                        break;
                    }
                }

                if (blockTransactions.size() == 3) {
                    blockTransactions.add(0, new Transaction(new User(), user, 16).toString());
                    Block newBlock = blockService.generateNextBlock(blockTransactions);
                    blockService.addBlock(newBlock);
                    p2pService.broadcast(p2pService.responseLatestMsg());
                    String string = JSON.toJSONString(newBlock, true);
                    System.out.println("Block added: " + string);
                    resp.getWriter().println(string);
                }
            } else {
                resp.getWriter().println("Illegal user");
            }
        }
    }

    private class PeersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            for (WebSocket socket : p2pService.getSockets()) {
                InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
                resp.getWriter().println(remoteSocketAddress.getHostName() + ":" + remoteSocketAddress.getPort());
            }
        }
    }

    private class AddPeerServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            this.doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            String peer = req.getParameter("peer");
            p2pService.connectToPeer(peer);
            resp.getWriter().println("Added a new peer");
        }
    }


    private class UsersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            for (User user : userService.getUserList()) {
                resp.getWriter().println(user.toString() + " : " + getBalance(user));
            }
        }
    }


    private class AddUserServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            this.doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            int node = req.getLocalPort();
            userService.registerUser(node);
            resp.getWriter().println("Registered a new user for " + node);
        }
    }

    private class TransferServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            this.doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            int address = Integer.parseInt(req.getParameter("user"));
            User payer = new User(req.getLocalPort(), address);
            if (userService.isValIdUser(payer)) {
                User payee = new User(req.getParameter("payee"));
                int amount = Integer.parseInt(req.getParameter("amount"));
                String result = sendGet("http://localhost:" + payee.getNode() + "/queryPayee", "address=" + payee.getAddress());
                if (result.equals("1")) {
                    result = sendGet("http://localhost:" + mainHost + "/addTransaction", "payer=" + payer.toString() + "&payee=" + payee.toString() + "&amount=" + amount);
                    if (result.equals("1")) {
                        resp.getWriter().println("Waiting for authorization");
                    } else {
                        resp.getWriter().println("Main host error");
                    }
                } else {
                    resp.getWriter().println("Illegal payee");
                }
            } else {
                resp.getWriter().println("Illegal payer");
            }
        }
    }

    private class TransactionServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            if (req.getLocalPort() == mainHost) {
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().println(JSON.toJSONString(transactions, true));
            }
        }
    }

    private class QueryPayeeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            User payee = new User(req.getLocalPort(), Integer.parseInt(req.getParameter("address")));
            if (userService.isValIdUser(payee)) {
                resp.getWriter().println("1");
            } else {
                resp.getWriter().println("0");
            }
        }
    }

    private class AddTransactionServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            if (req.getLocalPort() == mainHost) {
                resp.setCharacterEncoding("UTF-8");
                String payer = req.getParameter("payer");
                String payee = req.getParameter("payee");
                int amount = Integer.parseInt(req.getParameter("amount"));
                System.out.println(payer + payee + amount);
                transactions.add(new Transaction(new User(payer), new User(payee), amount));
                resp.getWriter().println("1");
            } else {
                resp.getWriter().println("0");
            }
        }
    }

    private static String sendGet(String url, String param) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.connect();

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            return "Server error";
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return result.toString();
    }

    private int getBalance(User user) {
        int balance = 0;
        for (Block block : this.blockService.getBlockChain()) {
            for (String transaction : block.getTransactions()) {
                String[] parameters = transaction.split("#");
                if (parameters[0].equals(user.toString())) {
                    balance -= Integer.parseInt(parameters[2]);
                }
                if (parameters[1].equals(user.toString())) {
                    balance += Integer.parseInt(parameters[2]);
                }
            }
        }
        return balance;
    }
}
