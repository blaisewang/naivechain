package org.naivechain.block;

/**
 * Created by sunysen on 2017/7/5.
 */


public class Main {
    public static void main(String[] args) {
        int mainHost = 0;
        if (args != null && (args.length == 2 || args.length == 3)) {
            try {
                int httpPort = Integer.valueOf(args[0]);
                int p2pPort = Integer.valueOf(args[1]);
                UserService userService = new UserService();
                if (args.length == 2) {
                    mainHost = httpPort;
                    userService.registerUser(httpPort);
                }
                BlockService blockService = new BlockService(mainHost);
                P2PService p2pService = new P2PService(blockService);
                p2pService.initP2PServer(p2pPort);
                if (args.length == 3 && args[2] != null) {
                    p2pService.connectToPeer(args[2]);
                }
                HTTPService httpService = new HTTPService(mainHost, blockService, userService, p2pService);
                httpService.initialHTTPServer(httpPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: java -jar naivechain.jar 3030 4001");
        }
    }
}
