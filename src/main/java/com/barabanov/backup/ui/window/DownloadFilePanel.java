package com.barabanov.backup.ui.window;

import com.barabanov.backup.service.ReliableBackupService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;


public class DownloadFilePanel extends JPanel
{

    public DownloadFilePanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout("",
                "[]5[]",
                "[]5[]5[]5[]10[]"));

        JLabel downloadLbl = new JLabel("Скачивание файла");
        downloadLbl.setFont(new Font("Dialog", Font.BOLD, 14));

        JLabel downloadIdLbl = new JLabel("Введите id файла:");
        JTextField downloadIdField = new JTextField(3);

        JLabel selectedDirectory = new JLabel();
        selectedDirectory.setMinimumSize(new Dimension(250, 5));

        JButton selectDirectoryBtn = new JButton("Выбрать директорию");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selectDirectoryBtn.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fileChooser.setDialogTitle("Выберите директорию");
                        if(fileChooser.showOpenDialog(DownloadFilePanel.this) == JFileChooser.APPROVE_OPTION)
                            selectedDirectory.setText(fileChooser.getSelectedFile().getName());
                    }
                }
        );

        JLabel SelectedDirectoryLbl = new JLabel("Выбранная директория: ");
        JButton downloadBtn = new JButton("Скачать");
        downloadBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backupService.downloadFile(passSupplier.get(), Long.parseLong(downloadIdField.getText()), fileChooser.getSelectedFile().getPath());
                downloadIdField.setText("");
                downloadIdField.repaint();
            }
        });

        this.add(downloadLbl, "span");
        this.add(SelectedDirectoryLbl);
        this.add(selectedDirectory, "wrap");
        this.add(downloadIdLbl);
        this.add(downloadIdField, "wrap");
        this.add(selectDirectoryBtn);
        this.add(downloadBtn);
    }
}
