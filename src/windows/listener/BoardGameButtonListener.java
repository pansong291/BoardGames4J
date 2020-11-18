package windows.listener;

import game.logic.RequestType;
import game.logic.Step;
import windows.BaseBoardGameFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author paso
 * @since 2020/11/14
 */
public class BoardGameButtonListener implements ActionListener {
    BaseBoardGameFrame frame;

    public BoardGameButtonListener(BaseBoardGameFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (frame.isRequesting) {
            return;
        }
        if (!frame.canRetract()) {
            return;
        }
        frame.isRequesting = true;
        Step step = new Step();
        if (e.getActionCommand().equals(frame.btnRetract.getActionCommand())) {
            frame.labelCurrent.setText("正在请求悔棋...");
            step.requestType = RequestType.RETRACT;
        } else if (e.getActionCommand().equals(frame.btnReset.getActionCommand())) {
            frame.labelCurrent.setText("正在发起新局...");
            step.requestType = RequestType.RESET;
        }
        frame.messagePool.putMessage(step);
        frame.btnRetract.setEnabled(false);
        frame.btnReset.setEnabled(false);
    }
} // ChineseChessButtonListener
