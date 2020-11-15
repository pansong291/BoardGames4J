package windows.listener;

import game.logic.ChineseChess;
import windows.ChineseChessFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author paso
 * @since 2020/11/14
 */
public class ChineseChessButtonListener implements ActionListener {
    ChineseChessFrame frame;

    public ChineseChessButtonListener(ChineseChessFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (frame.isRequesting) {
            return;
        }
        if (!frame.chessPanel.canRetract()) {
            return;
        }
        frame.isRequesting = true;
        ChineseChess.Step step = new ChineseChess.Step();
        if (e.getActionCommand().equals(frame.btnRetract.getActionCommand())) {
            frame.labelCurrent.setText("正在请求悔棋...");
            step.requestType = ChineseChess.RequestType.RETRACT;
        } else if (e.getActionCommand().equals(frame.btnReset.getActionCommand())) {
            frame.labelCurrent.setText("正在发起新局...");
            step.requestType = ChineseChess.RequestType.RESET;
        }
        frame.messagePool.putMessage(step);
        frame.btnRetract.setEnabled(false);
        frame.btnReset.setEnabled(false);
    }
} // ChineseChessButtonListener
