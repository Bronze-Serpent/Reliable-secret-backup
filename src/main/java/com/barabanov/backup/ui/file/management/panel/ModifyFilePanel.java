package com.barabanov.backup.ui.file.management.panel;

import com.barabanov.backup.service.ReliableBackupService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;


public class ModifyFilePanel extends JPanel
{

    public ModifyFilePanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout("",
                "[]5[]10[]",
                "[]5[]"));

        JLabel changePolicyLbl = new JLabel("Изменение политики отслеживания файла");
        changePolicyLbl.setFont(new Font("Dialog", Font.BOLD, 14));

        JLabel trackLbl = new JLabel("Введите id файла:");
        JTextField trackIdField = new JTextField(3);
        JButton trackButton = new JButton("Изменить");
        trackButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                backupService.changeTrackingPolicy(passSupplier.get(), Long.parseLong(trackIdField.getText()));
                trackIdField.setText("");
                trackIdField.repaint();
            }
        });


        this.add(changePolicyLbl, "span");
        this.add(trackLbl);
        this.add(trackIdField);
        this.add(trackButton, "span");

    }
}
