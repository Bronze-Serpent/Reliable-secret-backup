package com.barabanov.backup.ui.window;

import com.barabanov.backup.service.ReliableBackupService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;


public class ModifyFilePanel extends JPanel
{

    public ModifyFilePanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout("",
                "[]5[]10[]",
                "[]5[]15[]5[]"));

        JLabel changePolicyLbl = new JLabel("Изменение политики отслеживания файла");
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

        JLabel removeFileLbl = new JLabel("Удаление файла");
        JLabel removeLbl = new JLabel("Введите id файла:");
        JTextField removeIdField = new JTextField(3);
        JButton removeButton = new JButton("Удалить");
        removeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backupService.deleteFile(passSupplier.get(), Long.parseLong(removeIdField.getText()));
                removeIdField.setText("");
                removeIdField.repaint();
            }
        });


        this.add(changePolicyLbl, "span");
        this.add(trackLbl);
        this.add(trackIdField);
        this.add(trackButton, "span");
        this.add(removeFileLbl, "span");
        this.add(removeLbl);
        this.add(removeIdField);
        this.add(removeButton);
    }
}
