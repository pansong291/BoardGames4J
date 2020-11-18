package windows;

import dialog.ChooseDialog;
import dialog.MessageDialog;
import game.logic.ChineseChess;
import game.logic.Step;
import windows.panel.ChineseChessPanel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author paso
 * @since 2020/11/14
 */
public class ChineseChessFrame extends BaseBoardGameFrame {
    public ChineseChessPanel chessPanel;

    public ChineseChessFrame() {
        frame.setTitle("中国象棋对弈");
        labelCurrent.setText("红方");

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
                            btnRetract.setEnabled(true);
                            btnReset.setEnabled(true);
                        }
                    }).show();
        });
        frame.add(chessPanel);
        frame.setVisible(true);
        new ChooseDialog(this, frame);
    }

    @Override
    public boolean canRetract() {
        return chessPanel.canRetract();
    }

    @Override
    public void updateFrom(Step step, boolean fromOther) {
        chessPanel.updateFrom((ChineseChess.Step) step, fromOther);
    }

    @Override
    public void reset() {
        chessPanel.reset();
    }

    @Override
    public void retract4Request() {
        ChineseChess.Step retract = chessPanel.retract();
        if (retract != null && retract.after.color() != chessPanel.getPlayer()) {
            chessPanel.retract();
        }
    }

    @Override
    public void retract4Respond() {
        ChineseChess.Step retract = chessPanel.retract();
        if (retract != null && retract.after.color() == chessPanel.getPlayer()) {
            chessPanel.retract();
        }
    }

    @Override
    public String getLabelText() {
        return chessPanel.chineseChess.current == Color.RED ? "红方" : "黑方";
    }

    @Override
    public void startServer(String name, int port) {
        chessPanel.setPlayer(Color.RED);
        super.startServer(name, port);
    } // startServer

    @Override
    public void startClient(String name, String host, int port) {
        chessPanel.setPlayer(Color.BLACK);
        super.startClient(name, host, port);
    } // startClient
} // ChineseChessFrame
