package com.barabanov.backup.ui.window;

import com.barabanov.backup.service.FileType;
import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.ui.listener.ShowFilesListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;


public class FilesInfoPanel extends JPanel
{

    public FilesInfoPanel(ReliableBackupService backupService, char[] pass)
    {
        super(new MigLayout());

        JButton trackedFilesBtn = new JButton("Список отслеживаемых файлов");
        trackedFilesBtn.addActionListener(new ShowFilesListener(this, backupService, FileType.TRACKED, pass));

        JButton untrackedFilesBtn = new JButton("Список неотслеживаемых файлов");
        trackedFilesBtn.addActionListener(new ShowFilesListener(this, backupService, FileType.UNTRACKED, pass));

        JButton allFilesBtn = new JButton("Список сохранённых файлов");
        trackedFilesBtn.addActionListener(new ShowFilesListener(this, backupService, FileType.ALL, pass));

        this.add(trackedFilesBtn, "span");
        this.add(untrackedFilesBtn, "span");
        this.add(allFilesBtn, "span");
    }
}
