package com.barabanov.backup.ui.file.management;

import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.ui.file.management.panel.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.function.Supplier;


public class FileManagementPanel extends JPanel
{

    public FileManagementPanel(ReliableBackupService backupService, Supplier<char[]> passSupplier)
    {
        super(new MigLayout("insets 12",
                "[]",
                "[]20[]20[]20[]"));

        this.add(new SaveFilePanel(backupService, passSupplier), "wrap");
        this.add(new FilesInfoPanel(backupService, passSupplier), "wrap");
        this.add(new ModifyFilePanel(backupService, passSupplier), "wrap");
        this.add(new DownloadFilePanel(backupService, passSupplier), "wrap");
        this.add(new DeleteFilePanel(backupService, passSupplier));
    }
}
