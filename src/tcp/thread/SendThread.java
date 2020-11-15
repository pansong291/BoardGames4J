package tcp.thread;

import tcp.MessagePool;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author paso
 * @since 2020/11/14
 */
public class SendThread extends Thread {
    Socket socket;
    MessagePool messagePool;

    public SendThread(Socket socket, MessagePool messagePool) {
        this.socket = socket;
        this.messagePool = messagePool;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeUTF(messagePool.getMessage().toString());
            while (true) {
                if (messagePool.hasContent()) {
                    output.writeObject(messagePool.getMessage());
                } else {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
} // SendThread
