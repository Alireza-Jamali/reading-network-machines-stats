package client.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import javax.swing.JOptionPane;

public class XClientReqSim implements Runnable {

    String XData;

    public XClientReqSim(String s) {
        XData = s;
    }

    private final int PORT = 1234;
    private final String server = "192.168.7.183";

    private void sendInfo() {
        boolean sw = true;

        for (boolean b = true; b;) {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                Throwable localThrowable2 = null;
                try {
                    socketChannel.connect(new java.net.InetSocketAddress("192.168.7.183", 1234));

                    ByteBuffer buffer = ByteBuffer.allocate(512);
                    buffer.clear();

                    buffer.put(getHardwareInfo().getBytes());

                    buffer.flip();

                    while (buffer.hasRemaining()) {
                        socketChannel.write(buffer);
                    }

                    b = false;
                } catch (Throwable localThrowable1) {
                    localThrowable2 = localThrowable1;
                    throw localThrowable1;

                } finally {

                    if (socketChannel != null) {
                        if (localThrowable2 != null) {
                            try {
                                socketChannel.close();
                            } catch (Throwable x2) {
                                localThrowable2.addSuppressed(x2);
                            }
                        } else {
                            socketChannel.close();
                        }
                    }
                }
            } catch (IOException e) {
                int a = 0;
                if (sw) {
                    a = JOptionPane.showConfirmDialog(null, "Server is Offline, SubClient couldn't connect, would you like to wait?", "Server Offline", 0, 2);
                }

                switch (a) {
                    case 0:
                        sw = false;
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException es) {
                            System.out.println("error during sleep");
                            es.printStackTrace();
                        }

                    case 1:
                        System.exit(1);
                        break;
                    case 2:
                        System.exit(1);
                }

            }
        }
    }

    private String getHardwareInfo() {
        return XData;
    }

    public void run() {
        sendInfo();
    }
}
