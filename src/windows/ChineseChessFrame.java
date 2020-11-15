package windows;

import dialog.ChooseDialog;
import dialog.MessageDialog;
import game.logic.ChineseChess;
import tcp.MessagePool;
import tcp.client.DiyClient;
import tcp.server.DiyServer;
import windows.adapter.WinClose;
import windows.listener.ChineseChessButtonListener;
import windows.panel.ChineseChessPanel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author fanhuan
 * @date 2020/11/14
 */
public class ChineseChessFrame {
    Frame frame;
    public Label labelConnect, labelCurrent, labelStep;
    public ChineseChessPanel chessPanel;
    public Button btnRetract, btnReset;

    /**
     * 是否正在请求悔棋或新局操作
     */
    public boolean isRequesting;

    public MessagePool messagePool;

    public ChineseChessFrame() {
        messagePool = new MessagePool();
        frame = new Frame("中国象棋对弈");
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        labelConnect = new Label("待连接...");
        labelCurrent = new Label("红方");
        labelCurrent.setAlignment(Label.CENTER);
        labelStep = new Label();
        labelStep.setAlignment(Label.CENTER);
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(labelConnect);
        panel.add(labelCurrent);
        panel.add(labelStep);
        frame.add(panel, "North");
        chessPanel = new ChineseChessPanel(this);
        chessPanel.setOnClickListener(msg -> messagePool.putMessage(msg));
        chessPanel.setOnPieceDownListener(new ChineseChessPanel.OnPieceDownListener() {
            @Override
            public void onPieceDown(ChineseChess.Step s, Color cur) {
                labelStep.setText(s.stepName);
            }

            @Override
            public void afterPieceDown(Color cur) {
                labelCurrent.setText(cur == Color.RED ? "红方" : "黑方");
            }
        });
        chessPanel.setOnWinnerListener(cur -> {
            btnRetract.setEnabled(false);
            btnReset.setEnabled(false);
            new MessageDialog.Builder(frame)
                    .setSize(100, 100)
                    .setTitle("游戏结束")
                    .setMessage((cur == Color.RED ? "红" : "黑") + "方获胜!")
                    .setClosingListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            e.getWindow().dispose();
                            btnRetract.setEnabled(true);
                            btnReset.setEnabled(true);
                        }
                    }).show();
        });
        frame.add(chessPanel);
        panel = new Panel(new GridLayout(1, 2));
        btnReset = new Button("新局");
        btnRetract = new Button("悔棋");
        ChineseChessButtonListener listener = new ChineseChessButtonListener(this);
        btnReset.addActionListener(listener);
        btnRetract.addActionListener(listener);
        panel.add(btnRetract);
        panel.add(btnReset);
        frame.add(panel, "South");
        frame.addWindowListener(WinClose.WINDOW_ADAPTER_CLOSING);
        frame.setVisible(true);
        new ChooseDialog(this, frame);
    }

    public void request(ChineseChess.Step step) {
        switch (step.requestType) {
            case RESET:
                new MessageDialog.Builder(frame)
                        .setModal(true)
                        .setSize(100, 100)
                        .setMessage("对方发起新局")
                        .setPositiveButton("接受", e -> {
                            step.responseType = ChineseChess.ResponseType.AGREE;
                            messagePool.putMessage(step);
                            chessPanel.reset();
                        })
                        .setClosingListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                step.responseType = ChineseChess.ResponseType.REFUSE;
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
                            step.responseType = ChineseChess.ResponseType.AGREE;
                            messagePool.putMessage(step);
                            ChineseChess.Step retract = chessPanel.retract();
                            if (retract != null && retract.after.color() != chessPanel.getPlayer()) {
                                chessPanel.retract();
                            }
                            labelCurrent.setText(chessPanel.chineseChess.current == Color.RED ? "红方" : "黑方");
                        })
                        .setClosingListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                step.responseType = ChineseChess.ResponseType.REFUSE;
                                messagePool.putMessage(step);
                            }
                        })
                        .show();
                break;
            default:
        }
    } // request

    public void respond(ChineseChess.Step step) {
        switch (step.responseType) {
            case AGREE:
                switch (step.requestType) {
                    case RESET:
                        chessPanel.reset();
                        break;
                    case RETRACT:
                        ChineseChess.Step retract = chessPanel.retract();
                        if (retract != null && retract.after.color() == chessPanel.getPlayer()) {
                            chessPanel.retract();
                        }
                        break;
                    default:
                }
                break;
            case REFUSE:
                MessageDialog.show(frame, "", "对方拒绝了您的请求", true);
                break;
            default:
        }
        labelCurrent.setText(chessPanel.chineseChess.current == Color.RED ? "红方" : "黑方");
        isRequesting = false;
        btnReset.setEnabled(true);
        btnRetract.setEnabled(true);
    } // respond

    public void startServer(String name, int port) {
        chessPanel.setPlayer(Color.RED);
        messagePool.setName(name);
        new Thread() {
            @Override
            public void run() {
                new DiyServer(name, port, ChineseChessFrame.this).start();
            }
        }.start();
    } // startServer

    public void startClient(String name, String host, int port) {
        chessPanel.setPlayer(Color.BLACK);
        messagePool.setName(name);
        new Thread() {
            @Override
            public void run() {
                new DiyClient(name, host, port, ChineseChessFrame.this).start();
            }
        }.start();
    } // startClient
} // ChineseChessFrame
