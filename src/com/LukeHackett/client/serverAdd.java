package com.LukeHackett.client;

import javax.swing.*;
import java.awt.event.*;

public class serverAdd extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JFormattedTextField ipEntry;
    private JTextArea serverPrompt;

    serverAdd() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        ipEntry.addActionListener(e -> onOK());
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        String s = ipEntry.getText();
        ClientWindow.serverMap.put(ClientWindow.count++, s);
        ipEntry.setText("");
        setVisible(false);
    }

    private void onCancel() {
        // add your code here if necessary
        ipEntry.setText("");
        setVisible(false);
    }
}
