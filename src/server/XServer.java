/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author AezA
 */
import client.test.XClientReqSim;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class XServer
        implements Runnable {

    String xClientIp;
    CopyOnWriteArrayList<String> ipList = new CopyOnWriteArrayList();
    private final int PORT = 66;
    private Selector selector;
    InetSocketAddress address = new InetSocketAddress(66);

    public XServer(String s) {
        xClientIp = s;
    }

    public void run() {
        Thread XCSThread = new Thread(new XStatus(this), "XStatus");
        XCSThread.start();

        startServer();
    }

    void startServer() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Throwable localThrowable2 = null;
            try {
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(address);
                selector = Selector.open();
                serverSocketChannel.register(selector, 16);

                processConnections();
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;

            } finally {

                if (serverSocketChannel != null) {
                    if (localThrowable2 != null) {
                        try {
                            serverSocketChannel.close();
                        } catch (Throwable x2) {
                            localThrowable2.addSuppressed(x2);
                        }
                    } else {
                        serverSocketChannel.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void processConnections() {
        try {
            for (;;) {
                selector.select();

                Set eventKeys = selector.selectedKeys();
                Iterator keyCycler = eventKeys.iterator();

                while (keyCycler.hasNext()) {
                    SelectionKey key = (SelectionKey) keyCycler.next();

                    if (key.isAcceptable()) {
                        acceptConnection(key);

                    } else if ((key.isReadable()) || (key.isWritable())) {
                        acceptData(key);
                    }
                }
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            System.exit(1);
        }
    }

    private void acceptConnection(SelectionKey key) {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = ssc.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, 5);
            selector.selectedKeys().remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptData(SelectionKey key) {
        StringBuilder sb = new StringBuilder();
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            Throwable localThrowable2 = null;
            try {
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.clear();

                for (int z = 0; z != -1;) {
                    z = socketChannel.read(buffer);
                }

                buffer.flip();

                while (buffer.hasRemaining()) {
                    sb.append((char) buffer.get());
                }

                selector.selectedKeys().remove(key);

                String s = sb.toString();

                ipList.add(s.split("=")[1]);

                new Thread(new XClientReqSim(s));
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
            e.printStackTrace();
        }
    }

    CopyOnWriteArrayList<String> getIpList() {
        return ipList;
    }

    synchronized void manipulateTable(String s) {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            Throwable localThrowable2 = null;
            try {
                ssc.bind(new InetSocketAddress(99));
                SocketChannel sc = ssc.accept();
                ByteBuffer bf = ByteBuffer.allocate(64);
                bf.clear();
                bf.put(s.getBytes());
                bf.flip();
                while (bf.hasRemaining()) {
                    sc.write(bf);
                }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
