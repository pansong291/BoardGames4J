package windows.adapter;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author paso
 * @since 2020/11/14
 */
public class WinClose extends WindowAdapter {
    public static final WindowAdapter WINDOW_ADAPTER_CLOSING = new WinClose();

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
} // WinClose
