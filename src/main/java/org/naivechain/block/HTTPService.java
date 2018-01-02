package org.naivechain.block;

import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;

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
            context.addServlet(new ServletHolder(new UserServlet()), "/users");
            context.addServlet(new ServletHolder(new AddUserServlet()), "/addUser");
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
            resp.getWriter().println(JSON.toJSONString(blockService.getBlockChain(), true) + "\n");
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
            String peer = req.getParameterMap().get("peer")[0];
            p2pService.connectToPeer(peer);
            resp.getWriter().print("Added a new peer\n");
        }
    }


    private class PeersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(JSON.toJSONString(userService.getUserList(), true) + "\n");
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
            String peer = req.getParameterMap().get("peer")[0];
            userService.registerUser(peer);
            resp.getWriter().print("Registered a new user for");
        }
    }


    private class UserServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            for (WebSocket socket : p2pService.getSockets()) {
                InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
                resp.getWriter().print(remoteSocketAddress.getHostName() + ":" + remoteSocketAddress.getPort() + "\n");
            }
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
            String data = req.getParameterMap().get("data")[0];
            Block newBlock = blockService.generateNextBlock(data);
            blockService.addBlock(newBlock);
            p2pService.broadcast(p2pService.responseLatestMsg());
            String string = JSON.toJSONString(newBlock, true);
            System.out.println("Block added: " + string + "\n");
            resp.getWriter().print(string + "\n");
        }
    }
}
