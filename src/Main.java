import windows.ChineseChessFrame;
import windows.GobangFrame;
import windows.adapter.WinClose;

import java.awt.*;

/**
 * @author paso
 * @since 2020/11/14
 */
public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame("选择模式");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        Button btnChineseChess = new Button("中国象棋");
        Button btnGobang = new Button("五子棋");
        btnChineseChess.addActionListener(e -> {
            new ChineseChessFrame();
            frame.dispose();
        });
        btnGobang.addActionListener(e -> {
            new GobangFrame();
            frame.dispose();
        });
        Panel panel = new Panel(new GridLayout(2, 1));
        panel.add(btnChineseChess);
        panel.add(btnGobang);
        frame.add(panel);
        frame.addWindowListener(WinClose.WINDOW_ADAPTER_CLOSING);
        frame.setVisible(true);
    } // main

} // Main
