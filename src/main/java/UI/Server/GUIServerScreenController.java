package UI.Server;

import Services.Core.Extensions.UIExtensions;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUIServerScreenController extends JFrame {
    private JPanel mainPanel;
    private JTextField messageTextField;
    private JTextArea interlocutorTextArea;
    private JTextArea userTextArea;

    private final ServerScreenModel model;

    private GUIServerScreenController() {

        super();
        model = new ServerScreenModel();

        initUIComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,400);
        setLocation(UIExtensions.centerPosition(getSize()));
        setVisible(true);

        listenersSetup();
        subscribesSetup();
    }

    private void initUIComponents() {
        setTitle(model.localizeTitle());
        add(mainPanel);
    }

    private void listenersSetup() {
        messageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    model.sendMessage(messageTextField.getText());
                    userTextArea.append(messageTextField.getText());
                    userTextArea.append("\n");
                    messageTextField.setText("");
                }
            }
        });
    }

    private void subscribesSetup(){
        model.receiveMessagePublishSubject().subscribe(message -> {
            interlocutorTextArea.append(message);
            interlocutorTextArea.append("\n");
        });
    }

    public static void main(String[] args) {
        new GUIServerScreenController();
    }
}
