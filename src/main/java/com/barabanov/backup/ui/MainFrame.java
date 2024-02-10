package com.barabanov.backup.ui;

import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.ui.SecretHolder;
import com.barabanov.backup.ui.PasswordPanel;
import com.barabanov.backup.ui.file.management.FileManagementPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame
{

    public MainFrame(ReliableBackupService backupService, SecretHolder secretHolder)
    {
        super("Безопасное надёжное сохранение файлов");

        // третий параметр - другая панель, на которой все остальные панели
        PasswordPanel passwordPanel = new PasswordPanel(
                backupService,
                secretHolder::setPass,
                this,
                new FileManagementPanel(backupService, secretHolder::getPass)
        );

        this.add(passwordPanel);

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
















    private static class TestPanel extends JPanel
    {
        public TestPanel()
        {
            super(new MigLayout());

            this.add(new Label("Hello!"));
        }
    }
}
