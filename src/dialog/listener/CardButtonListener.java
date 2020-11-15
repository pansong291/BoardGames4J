package dialog.listener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author fanhuan
 * @date 2020/11/14
 */
public class CardButtonListener implements ActionListener {
    Panel cardPanel;
    CardLayout cardLayout;
    Button btn1;
    Button btn2;

    public CardButtonListener(Panel cardPanel, CardLayout cardLayout, Button btn1, Button btn2) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        this.btn1 = btn1;
        this.btn2 = btn2;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(btn1.getActionCommand())) {
            cardLayout.show(cardPanel, "card1");
            btn1.setEnabled(false);
            btn2.setEnabled(true);
        } else {
            cardLayout.show(cardPanel, "card2");
            btn1.setEnabled(true);
            btn2.setEnabled(false);
        }
    }
} // CardButtonListener
