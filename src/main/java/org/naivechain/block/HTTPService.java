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

/**
 * Created by sunysen on 2017/7/6.
 */
public class HTTPService {
    private BlockService blockService;
    private UserService userService;
    private P2PService p2pService;

    HTTPService(BlockService blockService, UserService userService, P2PService p2pService) {
        this.blockService = blockService;
        this.userService = userService;
        this.p2pService = p2pService;
    }

    public void initHTTPServer(int port) {
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
            context.addServlet(new ServletHolder(new ReceiveServlet()), "/receive");
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println("Initialization of HTTP Server error:" + e.getMessage());
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
            User user = new User(req.getLocalPort(), Integer.parseInt(req.getParameter("user")));
            if (userService.isValIdUser(user)) {
                Block newBlock = blockService.generateNextBlock(user);
                blockService.addBlock(newBlock);
                p2pService.broadcast(p2pService.responseLatestMsg());
                String string = JSON.toJSONString(newBlock, true);
                System.out.println("Block added: " + string);
                resp.getWriter().println(string);
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
            resp.getWriter().println(JSON.toJSONString(userService.getUserList(), true));
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
            try {
                int userAddress = Integer.parseInt(req.getParameter("user"));
                User user = new User(req.getLocalPort(), userAddress);
                if (userService.isValIdUser(user)) {
                    String hash = blockService.getMoneyHash(user);
                    if (!hash.equals("0")) {
                        int node = Integer.parseInt(req.getParameter("node"));
                        int transferAddress = Integer.parseInt(req.getParameter("address"));
                        String result = sendGet("http://localhost:" + node + "/receive", "address=" + transferAddress + "&hash=" + hash);
                        resp.getWriter().println(result);
                    } else {
                        resp.getWriter().println("Insufficient balance");
                    }
                } else {
                    resp.getWriter().println("Illegal user");
                }
            } catch (Exception e) {
                resp.getWriter().println("Illegal parameter(s)" + e.getMessage());
            }
        }
    }

    private class ReceiveServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            User user = new User(req.getLocalPort(), Integer.parseInt(req.getParameter("address")));
            if (userService.isValIdUser(user)) {
                String hash = req.getParameter("hash");
                blockService.setMoneyOwner(user, hash);
            } else {
                resp.getWriter().println("Nonexistent payee");
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
}
