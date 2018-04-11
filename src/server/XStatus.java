package server;

/**
 *
 * @author AezA
 */
import server.XServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CopyOnWriteArrayList;

class XStatus
        implements Runnable {

    String ip;
    XServer xServer;
    final int PORT = 787;

    XStatus(XServer server) {
        xServer = server;
    }

    public void run() {
        for (;;) {
            CopyOnWriteArrayList<String> ar = xServer.getIpList();
            for (String x : ar) {
                try {
                    SocketChannel sc = SocketChannel.open();
                    Throwable localThrowable2 = null;
                    try {
                        sc.connect(new InetSocketAddress(x, 787));
                    } catch (Throwable localThrowable1) {
                        localThrowable2 = localThrowable1;
                        throw localThrowable1;
                    } finally {
                        if (sc != null) {
                            if (localThrowable2 != null) {
                                try {
                                    sc.close();
                                } catch (Throwable x2) {
                                    localThrowable2.addSuppressed(x2);
                                }
                            } else {
                                sc.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    if ((e instanceof IOException & "Connection refused: connect".equals(e.getMessage()))) {
                        xServer.manipulateTable(x);
                    }
                }
            }
        }
    }
}
