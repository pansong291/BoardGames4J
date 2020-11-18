package windows;

import dialog.MessageDialog;
import game.logic.ResponseType;
import game.logic.Step;
import tcp.MessagePool;
import tcp.client.DiyClient;
import tcp.server.DiyServer;
import windows.adapter.WinClose;
import windows.listener.BoardGameButtonListener;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author paso
 * @since 2020/11/17
 */
public abstract class BaseBoardGameFrame {

    Frame frame;
    public Label labelConnect, labelCurrent, labelStep;
    public Button btnRetract, btnReset;
    /**
     * 是否正在请求悔棋或新局操作
     */
    public boolean isRequesting;
    public MessagePool messagePool;

    public BaseBoardGameFrame() {
        messagePool = new MessagePool();
        frame = new Frame();
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        labelConnect = new Label("待连接...");
        labelCurrent = new Label();
        labelCurrent.setAlignment(Label.CENTER);
        labelStep = new Label();
        labelStep.setAlignment(Label.CENTER);
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(labelConnect);
        panel.add(labelCurrent);
        panel.add(labelStep);
        frame.add(panel, "North");
        panel = new Panel(new GridLayout(1, 2));
        btnReset = new Button("新局");
        btnRetract = new Button("悔棋");
        BoardGameButtonListener listener = new BoardGameButtonListener(this);
        btnReset.addActionListener(listener);
        btnRetract.addActionListener(listener);
        panel.add(btnRetract);
        panel.add(btnReset);
        frame.add(panel, "South");
        frame.addWindowListener(WinClose.WINDOW_ADAPTER_CLOSING);
    }

    public void request(Step step) {
        switch (step.requestType) {
            case RESET:
                new MessageDialog.Builder(frame)
                        .setModal(true)
                        .setSize(100, 100)
                        .setMessage("对方发起新局")
                        .setPositiveButton("接受", e -> {
                            step.responseType = ResponseType.AGREE;
                            messagePool.putMessage(step);
                            reset();
                            labelCurrent.setText(getLabelText());
                        })
                        .setClosingListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                step.responseType = ResponseType.REFUSE;
                                messagePool.putMessage(step);
                            }
                        })
                        .show();
                break;
            case RETRACT:
                new MessageDialog.Builder(frame)
                        .setModal(true)
                        .setSize(100, 100)
                        .setMessage("对方请求悔棋")
                        .setPositiveButton("同意", e -> {
                            step.responseType = ResponseType.AGREE;
                            messagePool.putMessage(step);
                            retract4Request();
                            labelCurrent.setText(getLabelText());
                        })
                        .setClosingListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                step.responseType = ResponseType.REFUSE;
                                messagePool.putMessage(step);
                            }
                        })
                        .show();
                break;
            default:
        }
    } // request

    public void respond(Step step) {
        switch (step.responseType) {
            case AGREE:
                switch (step.requestType) {
                    case RESET:
                        reset();
                        break;
                    case RETRACT:
                        retract4Respond();
                        break;
                    default:
                }
                break;
            case REFUSE:
                MessageDialog.show(frame, "", "对方拒绝了您的请求", true);
                break;
            default:
        }
        labelCurrent.setText(getLabelText());
        isRequesting = false;
        btnReset.setEnabled(true);
        btnRetract.setEnabled(true);
    } // respond

    public abstract boolean canRetract();

    public abstract void updateFrom(Step step, boolean fromOther);

    public abstract void reset();

    public abstract void retract4Request();

    public abstract void retract4Respond();

    public abstract String getLabelText();

    public void startServer(String name, int port) {
        messagePool.setName(name);
        new Thread() {
            @Override
            public void run() {
                new DiyServer(name, port, BaseBoardGameFrame.this).start();
            }
        }.start();
    } // startServer

    public void startClient(String name, String host, int port) {
        messagePool.setName(name);
        new Thread() {
            @Override
            public void run() {
                new DiyClient(name, host, port, BaseBoardGameFrame.this).start();
            }
        }.start();
    } // startClient
} // BaseBoardGameFrame
