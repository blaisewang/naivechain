package org.naivechain.block;

/**
 * Created by sunysen on 2017/7/5.
 */
public class Main {
    private final static int MAIN_HOST = 3030;

    public static void main(String[] args) {
        if (args != null && (args.length == 2 || args.length == 3)) {
            try {
                int httpPort = Integer.valueOf(args[0]);
                int p2pPort = Integer.valueOf(args[1]);
                UserService userService = new UserService();
                if (args.length == 2) {
                    userService.registerUser(httpPort);
                }
                BlockService blockService = new BlockService(MAIN_HOST);
                P2PService p2pService = new P2PService(blockService);
                p2pService.initP2PServer(p2pPort);
                if (args.length == 3 && args[2] != null) {
                    p2pService.connectToPeer(args[2]);
                }
                HTTPService httpService = new HTTPService(blockService, userService, p2pService);
                httpService.initialHTTPServer(httpPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: java -jar naivechain.jar 3030 4001");
        }
    }
}
