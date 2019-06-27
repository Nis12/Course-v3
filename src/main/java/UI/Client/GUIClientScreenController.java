package UI.Client;

import Services.Core.Extensions.UIExtensions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUIClientScreenController extends JFrame {
    private JPanel mainPanel;
    private JPanel screenPanel;
    private JPanel messagePanel;
    private JTextField messageTextField;
    private JTextArea messagesTextArea;

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
        messagesTextArea.setAutoscrolls(true);
        messagesTextArea.setAlignmentX(TextArea.RIGHT_ALIGNMENT);
        getContentPane().add(mainPanel);
    }

    private void listenersSetup() {
        messageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    model.sendMessage(messageTextField.getText());
                    messagesTextArea.append(messageTextField.getText());
                    messagesTextArea.append("\n");
                    messageTextField.setText("");
                }
            }
        });
    }

    private void subscribesSetup() {
        model.receiveMessagePublishSubject().subscribe(message -> {
            messagesTextArea.append(message);
            messagesTextArea.append("\n");
        });
    }

    public static void main(String[] args) {
        new GUIClientScreenController(args[0]);
    }
}
