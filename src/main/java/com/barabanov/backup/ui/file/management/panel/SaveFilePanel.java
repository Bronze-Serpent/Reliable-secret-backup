package com.barabanov.backup.ui.file.management.panel;

import com.barabanov.backup.service.ReliableBackupService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;


public class SaveFilePanel extends JPanel
{
    public SaveFilePanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout(
                "",
                "[]5[]",
                "[]5[]5[]5[]10[]"
        ));

        JLabel selectedPathLbl = new JLabel();
        selectedPathLbl.setMinimumSize(new Dimension(250, 5));
        JFileChooser fileChooser = new JFileChooser();

        JButton selectFileBtn = new JButton("Выбрать файл");
        selectFileBtn.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        fileChooser.setDialogTitle("Открыть файл");
                        if(fileChooser.showOpenDialog(SaveFilePanel.this) == JFileChooser.APPROVE_OPTION)
                            selectedPathLbl.setText(fileChooser.getSelectedFile().getName());

                        SaveFilePanel.this.repaint();
                    }
                }
        );

        JLabel selectedFileLbl = new JLabel("Выбранный файл:");
        JLabel trackLbl = new JLabel("Отслеживать целостность");
        JCheckBox trackCheckBox = new JCheckBox();

        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        backupService.saveFile(passSupplier.get(), fileChooser.getSelectedFile().getPath(), trackCheckBox.isSelected());
                        selectedPathLbl.setText("");
                        trackCheckBox.setSelected(false);
                        SaveFilePanel.this.repaint();
                    }
                }
        );

        JLabel saveFileLbl = new JLabel("Сохранение файла");
        saveFileLbl.setFont(new Font("Dialog", Font.BOLD, 14));
        this.add(saveFileLbl, "span");
        this.add(selectedFileLbl);
        this.add(selectedPathLbl, "wrap");
        this.add(trackLbl);
        this.add(trackCheckBox, "wrap");
        this.add(selectFileBtn);
        this.add(saveBtn);
    }
}
