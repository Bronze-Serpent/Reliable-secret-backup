package com.barabanov.backup.ui.window;

import com.barabanov.backup.service.ReliableBackupService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;

public class DeleteFilePanel extends JPanel
{

    public DeleteFilePanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout("",
                "[]5[]10[]",
                "[]5[]"));

        JLabel removeFileLbl = new JLabel("Удаление файла");
        removeFileLbl.setFont(new Font("Dialog", Font.BOLD, 14));

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

        this.add(removeFileLbl, "span");
        this.add(removeLbl);
        this.add(removeIdField);
        this.add(removeButton);
    }
}
