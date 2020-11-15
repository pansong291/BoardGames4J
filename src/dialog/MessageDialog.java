package dialog;

import java.awt.*;
import java.awt.event.*;

/**
 * @author paso
 * @since 2020/11/14
 */
public class MessageDialog {
    public static void show(Frame owner, String title, String msg, boolean model) {
        Dialog dialog = new Dialog(owner, title, model);
        int x = owner.getX() + owner.getWidth() / 2;
        int y = owner.getY() + owner.getHeight() / 2;
        dialog.setBounds(x - 50, y - 50, 100, 100);
        dialog.add(new Label(msg, Label.CENTER));
        dialog.addWindowListener(DialogClose.DIALOG_ADAPTER_CLOSING);
        dialog.setVisible(true);
    } // show

    public static class Builder {
        Frame frame;
        Dialog dialog;

        public Builder(Frame owner) {
            frame = owner;
            dialog = new Dialog(owner);
        }

        public Builder setModal(boolean modal) {
            dialog.setModal(modal);
            return this;
        } // setModal

        public Builder setSize(int w, int h) {
            int x = frame.getX() + frame.getWidth() / 2 - w / 2;
            int y = frame.getY() + frame.getHeight() / 2 - h / 2;
            dialog.setBounds(x, y, w, h);
            return this;
        } // setSize

        public Builder setTitle(String t) {
            dialog.setTitle(t);
            return this;
        } // setTitle

        public Builder setMessage(String msg) {
            dialog.add(new Label(msg, Label.CENTER));
            return this;
        } // setMessage

        public Builder setPositiveButton(String btnTitle, ActionListener listener) {
            Button btn = new Button(btnTitle);
            btn.addActionListener(e -> {
                listener.actionPerformed(e);
                dialog.dispose();
            });
            dialog.add(btn, "South");
            return this;
        } // setPositiveButton

        public Builder setClosingListener(WindowListener listener) {
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    listener.windowClosing(e);
                    dialog.dispose();
                }
            });
            return this;
        } // setClosingListener

        public void show() {
            dialog.setVisible(true);
        } // show
    } // Builder
} // MessageDialog
