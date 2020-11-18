package tcp.server;

import tcp.thread.ReceiveThread;
import tcp.thread.SendThread;
import windows.BaseBoardGameFrame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author fanhuan
 * @date 2020/11/14
 */
public class DiyServer {
    String name;
    ServerSocket serverSocket;
    BaseBoardGameFrame frame;

    public DiyServer(String name, int port, BaseBoardGameFrame frame) {
        try {
            this.name = name;
            this.frame = frame;
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            String address = " [" + InetAddress.getLocalHost().getHostAddress() +
                    ":" + serverSocket.getLocalPort() + "]";
            frame.labelConnect.setText("等待连接" + address);
            frame.messagePool.putMessage(name + address);
            Socket socket = serverSocket.accept();
            new ReceiveThread(socket, frame).start();
            new SendThread(socket, frame.messagePool).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // start

} // DiyServer
