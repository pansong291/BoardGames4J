package tcp.thread;

import game.logic.ChineseChess;
import windows.ChineseChessFrame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * @author fanhuan
 * @date 2020/11/14
 */
public class ReceiveThread extends Thread {
    Socket socket;
    ChineseChessFrame frame;

    public ReceiveThread(Socket socket, ChineseChessFrame frame) {
        this.socket = socket;
        this.frame = frame;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            frame.labelConnect.setText(input.readUTF());
            while (true) {
                ChineseChess.Step step = (ChineseChess.Step) input.readObject();
                if (step.responseType != null) {
                    frame.respond(step);
                } else if (step.requestType != null) {
                    frame.request(step);
                } else {
                    frame.chessPanel.updateFrom(step, true);
                }
                TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (SocketException se) {
            frame.labelConnect.setText("连接丢失");
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
} // ReceiveThread
