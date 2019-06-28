package UI.Client;

import Services.Core.Extensions.UIExtensions;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUIClientScreenController extends JFrame {
    private JPanel mainPanel;
    private JPanel screenPanel;
    private JPanel messagePanel;
    private JTextField messageTextField;
    private JScrollPane messageScrollPane;

    private final ClientScreenModel model;

    private GUIClientScreenController(String ipAddress) {

        super();
        model = new ClientScreenModel(ipAddress);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initUIComponents();
        setSize(400,400);
        setLocation(UIExtensions.centerPosition(getSize()));
        setVisible(true);

        listenersSetup();
        subscribesSetup();
    }

    private void initUIComponents() {
        setTitle(model.localizeTitle());
        messageScrollPane.setAutoscrolls(true);
        getContentPane().add(mainPanel);
    }

    private void listenersSetup() {
        messageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    model.sendMessage(messageTextField.getText());
                    addMessageInStoryList(messageTextField.getText(), false);
                    messageTextField.setText("");
                }
            }
        });
    }

    private void addMessageInStoryList(String message, boolean isReceive) {
        mainPanel.add(new subPanel(message));
    }

    private void subscribesSetup() {
        model.receiveMessagePublishSubject().subscribe(message -> addMessageInStoryList(message,true));
    }

    private class subPanel extends JPanel {

        subPanel me;

        subPanel(String message) {
            super();
            me = this;
            JLabel myLabel = new JLabel(message);
            add(myLabel);
        }
    }

    public static void main(String[] args) {
        new GUIClientScreenController(args[0]);
    }
}
