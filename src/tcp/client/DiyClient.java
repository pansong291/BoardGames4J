package tcp.client;

import tcp.thread.ReceiveThread;
import tcp.thread.SendThread;
import windows.ChineseChessFrame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author fanhuan
 * @date 2020/11/14
 */
public class DiyClient {
    String name;
    Socket socket;
    ChineseChessFrame frame;

    public DiyClient(String name, String host, int port, ChineseChessFrame frame) {
        try {
            this.name = name;
            this.frame = frame;
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            String address = " [" + InetAddress.getLocalHost().getHostAddress() +
                    ":" + socket.getLocalPort() + "]";
            frame.messagePool.putMessage(name + address);
            new ReceiveThread(socket, frame).start();
            new SendThread(socket, frame.messagePool).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // start
} // DiyClient
