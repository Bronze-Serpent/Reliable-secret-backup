package com.barabanov.backup.ui.file.management.panel;

import com.barabanov.backup.service.FileType;
import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.ui.file.management.listener.ShowFilesListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;


public class FilesInfoPanel extends JPanel
{

    public FilesInfoPanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout(
                "wrap",
                "[]",
                "[]5[]5[]5[]5[]"
        ));
        JLabel showFileLbl = new JLabel("Просмотр файлов");
        showFileLbl.setFont(new Font("Dialog", Font.BOLD, 14));
        Dimension showBtnSize = new Dimension(250, 20);

        JButton trackedFilesBtn = new JButton("Список отслеживаемых файлов");
        trackedFilesBtn.setMinimumSize(showBtnSize);
        trackedFilesBtn.addActionListener(new ShowFilesListener(this, backupService, FileType.TRACKED, passSupplier));

        JButton untrackedFilesBtn = new JButton("Список неотслеживаемых файлов");
        untrackedFilesBtn.setMinimumSize(showBtnSize);
        untrackedFilesBtn.addActionListener(new ShowFilesListener(this, backupService, FileType.UNTRACKED, passSupplier));

        JButton allFilesBtn = new JButton("Список сохранённых файлов");
        allFilesBtn.setMinimumSize(showBtnSize);
        allFilesBtn.addActionListener(new ShowFilesListener(this, backupService, FileType.ALL, passSupplier));

        this.add(showFileLbl);
        this.add(showFileLbl);
        this.add(trackedFilesBtn);
        this.add(untrackedFilesBtn);
        this.add(allFilesBtn);
    }
}
