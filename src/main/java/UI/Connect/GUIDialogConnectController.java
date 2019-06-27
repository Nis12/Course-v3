package UI.Connect;

import Services.Core.Extensions.UIExtensions;
import UI.Client.GUIClientScreenController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

public class GUIDialogConnectController extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel ipAddressLabel;
    private JFormattedTextField ipAddressFormattedTextField;

    private DialogConnectModel model = new DialogConnectModel();

    private GUIDialogConnectController() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        licalizable();
        initUIComponents();
        listenersSetup();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                        0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initUIComponents() {
        try {
            ipAddressFormattedTextField.setBorder(new LineBorder(Color.GRAY,1));
            MaskFormatter f = new MaskFormatter("###.###.###.###");
            f.setValidCharacters("0123456789 ");
            f.install(ipAddressFormattedTextField);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void listenersSetup() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        ipAddressFormattedTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    String s = ipAddressFormattedTextField.getText();
                    System.out.println(s.replace(' ', '\0'));
                } else if (e.getKeyChar() == KeyEvent.VK_SPACE) {
                    int caretPosition = ipAddressFormattedTextField.getCaretPosition();

                    switch (caretPosition) {
                        case 1:
                        case 5:
                        case 9:
                            caretPosition += 3;
                            break;
                        case 2:
                        case 6:
                        case 10:
                            caretPosition += 2;
                            break;
                        case 3:
                        case 7:
                        case 11:
                            caretPosition += 1;
                            break;
                        case 0:
                        case 4:
                        case 8:
                        case 12:
                            new Thread(() -> {
                                try {
                                    int timer = 0;
                                    while (timer++ != 3) {
                                        ipAddressFormattedTextField.setBorder(new LineBorder(Color.RED,2));
                                        Thread.sleep(300);
                                        ipAddressFormattedTextField.setBorder(new LineBorder(Color.GRAY,2));
                                        Thread.sleep(300);
                                    }
                                    ipAddressFormattedTextField.setBorder(new LineBorder(Color.GRAY,1));
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                            break;
                        default:
                            break;

                    }
                    ipAddressFormattedTextField.setCaretPosition(caretPosition);
                }

            }
        });
    }

    private void onOK() {
        //String[] args = {ipAddressFormattedTextField.getText()};
        String[] args = {"localhost"};
        GUIClientScreenController.main(args);
        dispose();
    }

    private void onCancel() {
        System.exit(0);
    }

    private void licalizable() {
        setTitle(model.localizeTitle());
        ipAddressLabel.setText(model.localizeIPAddressLabel());
    }

    public static void main(String[] args) {
        GUIDialogConnectController dialog = new GUIDialogConnectController();
        dialog.pack();
        dialog.setLocation(UIExtensions.centerPosition(dialog.getSize()));
        dialog.setVisible(true);
    }
}
