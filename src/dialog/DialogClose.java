package dialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author paso
 * @since 2020/11/14
 */
public class DialogClose extends WindowAdapter {
    public static final WindowAdapter DIALOG_ADAPTER_CLOSING = new DialogClose();

    @Override
    public void windowClosing(WindowEvent e) {
        e.getWindow().dispose();
    }
} // DialogClose
