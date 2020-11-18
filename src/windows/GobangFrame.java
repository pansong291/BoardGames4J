package windows;

import dialog.ChooseDialog;
import dialog.MessageDialog;
import game.logic.Gobang;
import game.logic.Step;
import windows.panel.GobangPanel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author paso
 * @since 2020/11/17
 */
public class GobangFrame extends BaseBoardGameFrame {

    public GobangPanel gobangPanel;

    public GobangFrame() {
        frame.setTitle("五子棋对弈");
        labelCurrent.setText("黑方");

        gobangPanel = new GobangPanel(this);
        gobangPanel.setOnClickListener(msg -> messagePool.putMessage(msg));
        gobangPanel.setOnPieceDownListener(new GobangPanel.OnPieceDownListener() {
            @Override
            public void onPieceDown(Point p, Gobang.BoardState bs) {
            }

            @Override
            public void afterPieceDown(Gobang.BoardState bs) {
                labelCurrent.setText(bs == Gobang.BoardState.BLACK ? "黑方" : "白方");
            }
        });
        gobangPanel.setOnWinnerListener(bs -> {
            btnRetract.setEnabled(false);
            btnReset.setEnabled(false);
            String str = bs == null ? "平局!" : ((bs == Gobang.BoardState.BLACK ? "黑" : "白") + "方获胜!");
            new MessageDialog.Builder(frame)
                    .setSize(100, 100)
                    .setTitle("游戏结束")
                    .setMessage(str)
                    .setClosingListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            btnRetract.setEnabled(true);
                            btnReset.setEnabled(true);
                        }
                    }).show();
        });
        frame.add(gobangPanel);
        frame.setVisible(true);
        new ChooseDialog(this, frame);
    }

    @Override
    public boolean canRetract() {
        return gobangPanel.gobang.canRetract();
    }

    @Override
    public void updateFrom(Step step, boolean fromOther) {
        gobangPanel.updateFrom((Gobang.Step) step, fromOther);
    }

    @Override
    public void reset() {
        gobangPanel.reset();
    }

    @Override
    public void retract4Request() {
        Gobang.Step retract = gobangPanel.retract();
        if (retract != null && retract.boardState == gobangPanel.player) {
            gobangPanel.retract();
        }
    }

    @Override
    public void retract4Respond() {
        Gobang.Step retract = gobangPanel.retract();
        if (retract != null && retract.boardState != gobangPanel.player) {
            gobangPanel.retract();
        }
    }

    @Override
    public String getLabelText() {
        return gobangPanel.gobang.current == Gobang.BoardState.BLACK ? "黑方" : "白方";
    }

    @Override
    public void startServer(String name, int port) {
        gobangPanel.player = Gobang.BoardState.BLACK;
        super.startServer(name, port);
    } // startServer

    @Override
    public void startClient(String name, String host, int port) {
        gobangPanel.player = Gobang.BoardState.WHITE;
        super.startClient(name, host, port);
    } // startClient
} // GobangFrame
