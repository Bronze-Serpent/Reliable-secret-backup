package com.barabanov.backup.ui.window;

import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.ui.SecretHolder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


public class ReliableBackupFrame extends JFrame
{

    public ReliableBackupFrame(ReliableBackupService backupService)
    {
        super("Безопасное надёжное сохранение файлов");
        SecretHolder secretHolder = new SecretHolder();

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
