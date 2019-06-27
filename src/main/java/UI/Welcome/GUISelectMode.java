package UI.Welcome;

import Services.Core.Extensions.LocalizableExtension;
import Services.Core.Extensions.UIExtensions;
import UI.Connect.GUIDialogConnectController;
import UI.Server.GUIServerScreenController;

import javax.swing.*;
import java.awt.event.*;

public class GUISelectMode extends JDialog {
    private JPanel contentPane;
    private JButton clientModeButton;
    private JButton buttonCancel;
    private JButton serverModeButton;
    private JLabel welcomeMessageLabel;

    private String[] args;

    private GUISelectMode() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(clientModeButton);
        licalizable();

        clientModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startClient();
            }
        });

        serverModeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void startClient() {
        GUIDialogConnectController.main(args);
        dispose();
    }

    private void startServer() {
        GUIServerScreenController.main(args);
        dispose();
    }

    private void onCancel() {
        System.exit(0);
    }

    private void licalizable() {
        setTitle(LocalizableExtension.getBrowserLocalizer().getLocalizedText("welcome_title"));
        welcomeMessageLabel.setText(LocalizableExtension.getBrowserLocalizer().getLocalizedText("welcome_message"));
    }

    public static void main(String[] args) {
        GUISelectMode dialog = new GUISelectMode();
        dialog.args = args;
        dialog.pack();
        dialog.setLocation(UIExtensions.centerPosition(dialog.getSize()));
        dialog.setVisible(true);
    }
}
