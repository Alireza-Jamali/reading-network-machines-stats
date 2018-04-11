package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import javax.swing.JOptionPane;

public class SubClientInfo {

    private final int PORT = 66;
    private final String server = "192.168.7.185";

    public SubClientInfo() {
    }

    private void sendInfo() {
        boolean sw = true;

        for (boolean b = true; b;) {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                Throwable localThrowable2 = null;
                try {
                    socketChannel.connect(new InetSocketAddress("192.168.7.185", 66));

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
                    a = JOptionPane.showConfirmDialog(null, "Server is Offline, Would you like to wait for the server?", "Server Offline", 0, 2);
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
        StringBuilder stb = new StringBuilder();

        try {
            InetAddress ip = InetAddress.getLocalHost();
            stb.append(ip.getHostName() + "(SubClient)" + "=");
            stb.append(ip.getHostAddress() + "=");
            String nameOS = System.getProperty("os.name");
            stb.append(nameOS + "=");
            String osType = System.getProperty("os.arch");
            stb.append(osType + "=");
            String osVersion = System.getProperty("os.version");
            stb.append(osVersion + "=");

            stb.append(System.getenv("PROCESSOR_IDENTIFIER") + "=");
            stb.append(System.getenv("PROCESSOR_ARCHITECTURE") + "=");
            stb.append(System.getenv("NUMBER_OF_PROCESSORS") + "=");

            stb.append(Runtime.getRuntime().availableProcessors() + "=");

            stb.append(Runtime.getRuntime().freeMemory() + "=");

            long maxMemory = Runtime.getRuntime().maxMemory();

            stb.append((maxMemory == Long.MAX_VALUE ? "no limit" : Long.valueOf(maxMemory)) + "=");

            stb.append(Runtime.getRuntime().totalMemory() + "=");

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", new Object[]{Byte.valueOf(mac[i]), i < mac.length - 1 ? "-" : ""}));
            }
            stb.append(sb.toString() + "=");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stb.toString();
    }

    public static void main(String[] args) {
        SubClientInfo sci = new SubClientInfo();

        sci.pingThread().start();

        sci.sendInfo();
    }

    private Thread pingThread() {
        Thread tr = new Thread(new Runnable() {
            public void run() {
                try {
                    for (;;) {
                        ServerSocketChannel ssc = ServerSocketChannel.open();
                        Throwable localThrowable2 = null;
                        try {
                            ssc.bind(new InetSocketAddress(787));
                            ssc.accept();
                        } catch (Throwable localThrowable1) {
                            localThrowable2 = localThrowable1;
                            throw localThrowable1;

                        } finally {
                            if (ssc != null) {
                                if (localThrowable2 != null) {
                                    try {
                                        ssc.close();
                                    } catch (Throwable x2) {
                                        localThrowable2.addSuppressed(x2);
                                    }
                                } else {
                                    ssc.close();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });
        return tr;
    }
}
