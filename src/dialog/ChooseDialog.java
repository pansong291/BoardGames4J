package dialog;

import dialog.listener.CardButtonListener;
import windows.BaseBoardGameFrame;
import windows.adapter.WinClose;

import java.awt.*;

/**
 * @author paso
 * @since 2020/11/14
 */
public class ChooseDialog {
    BaseBoardGameFrame mainFrame;

    Dialog dialog;

    Panel panelCard;
    Button btnCardServer;
    Button btnCardClient;

    Panel panelCenter;

    Panel panelServer;
    Panel row11;
    Panel row12;
    Label labelName1;
    Label labelPort1;
    TextField textFieldName1;
    TextField textFieldPort1;

    Panel panelClient;
    Panel row21;
    Panel row22;
    Panel row23;
    Label labelName2;
    Label labelHost2;
    Label labelPort2;
    TextField textFieldName2;
    TextField textFieldHost2;
    TextField textFieldPort2;

    Button btnOk;

    public ChooseDialog(BaseBoardGameFrame mFrame, Frame frame) {
        this.mainFrame = mFrame;
        dialog = new Dialog(frame, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 300);
        dialog.setLocationRelativeTo(null);
        dialog.addWindowListener(WinClose.WINDOW_ADAPTER_CLOSING);
        panelCard = new Panel();
        btnCardServer = new Button("创建房间");
        btnCardClient = new Button("加入房间");
        btnCardServer.setEnabled(false);

        CardLayout cardLayout = new CardLayout();
        panelCenter = new Panel(cardLayout);
        CardButtonListener cardButtonListener = new CardButtonListener(
                panelCenter, cardLayout, btnCardServer, btnCardClient);
        btnCardServer.addActionListener(cardButtonListener);
        btnCardClient.addActionListener(cardButtonListener);

        panelServer = new Panel(new GridLayout(2, 1));
        row11 = new Panel();
        row12 = new Panel();
        labelName1 = new Label("昵称:");
        labelPort1 = new Label("端口:");
        textFieldName1 = new TextField(20);
        textFieldPort1 = new TextField(20);
        panelClient = new Panel(new GridLayout(3, 1));
        row21 = new Panel();
        row22 = new Panel();
        row23 = new Panel();
        labelName2 = new Label("昵称: ");
        labelHost2 = new Label("IP地址: ");
        labelPort2 = new Label("端口: ");
        textFieldName2 = new TextField(20);
        textFieldHost2 = new TextField(20);
        textFieldPort2 = new TextField(20);
        btnOk = new Button("确定");
        btnOk.addActionListener(actionEvent -> {
            if (!btnCardServer.isEnabled()) {
                if (!checkTextNonEmpty(textFieldName1)) {
                    return;
                }
                if (!checkTextNonEmpty(textFieldPort1)) {
                    return;
                }
                String portStr = textFieldPort1.getText();
                try {
                    int port = Integer.parseInt(portStr);
                    mainFrame.startServer(textFieldName1.getText(), port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (!checkTextNonEmpty(textFieldName2)) {
                    return;
                }
                if (!checkTextNonEmpty(textFieldHost2)) {
                    return;
                }
                if (!checkTextNonEmpty(textFieldPort2)) {
                    return;
                }
                String portStr = textFieldPort2.getText();
                try {
                    int port = Integer.parseInt(portStr);
                    mainFrame.startClient(textFieldName2.getText(), textFieldHost2.getText(), port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dialog.setVisible(false);
        });

        dialog.add(panelCard, "North");
        dialog.add(panelCenter);
        dialog.add(btnOk, "South");

        panelCard.add(btnCardServer);
        panelCard.add(btnCardClient);

        panelCenter.add(panelServer, "card1");
        panelCenter.add(panelClient, "card2");

        panelServer.add(row11);
        panelServer.add(row12);

        panelClient.add(row21);
        panelClient.add(row22);
        panelClient.add(row23);

        row11.add(labelName1);
        row11.add(textFieldName1);
        row12.add(labelPort1);
        row12.add(textFieldPort1);

        row21.add(labelName2);
        row21.add(textFieldName2);
        row22.add(labelHost2);
        row22.add(textFieldHost2);
        row23.add(labelPort2);
        row23.add(textFieldPort2);
        dialog.setVisible(true);
    }

    public boolean checkTextNonEmpty(TextField textField) {
        String text = textField.getText();
        return text != null && !text.isEmpty();
    } // checkTextNonEmpty

} // ChooseDialog
