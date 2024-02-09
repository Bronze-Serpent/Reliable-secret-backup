package com.barabanov.backup.ui.window;

import com.barabanov.backup.service.ReliableBackupService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class PasswordPanel extends JPanel
{

    public PasswordPanel(ReliableBackupService backupService,
                         Consumer<char[]> passConsumer,
                         Window parentWindow,
                         JPanel toDisplayPanelSupplier)
    {
        super(new MigLayout(
                "",
                "[]10[]",
                "[]30[]10[]"
        ));

        JLabel registerLbl = new JLabel("Укажите пароль. Он будет использоваться для шифрования");
        JLabel passLbl = new JLabel("Пароль: ");
        JTextField passTextField = new JTextField(30);

        JButton passSubmitBtn = new JButton("ОК");
        passSubmitBtn.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                initBackupInfrastructure(backupService, passTextField.getText().toCharArray());

                passConsumer.accept(passTextField.getText().toCharArray());

                parentWindow.remove(PasswordPanel.this);
                parentWindow.add(toDisplayPanelSupplier);
                parentWindow.pack();
                parentWindow.repaint();
            }
        });

        this.add(registerLbl, "span");
        this.add(passLbl);
        this.add(passTextField);
        this.add(passSubmitBtn);
    }

    private void initBackupInfrastructure(ReliableBackupService backupService, char[] pass)
    {
        if (!backupService.isFileAreOnDisk())
        {
            backupService.createInitCloudElements(pass);
        }
    }
}
