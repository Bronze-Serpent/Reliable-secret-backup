package com.barabanov.backup.ui.window;

import com.barabanov.backup.ui.SecretHolder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


public class ReliableBackupFrame extends JFrame
{
    private final SecretHolder secretHolder;


    public ReliableBackupFrame()
    {
        super("Безопасное надёжное сохранение файлов");
        this.secretHolder = new SecretHolder();

        // третий параметр - другая панель, на которой все остальные панели
        PasswordPanel passwordPanel = new PasswordPanel(
                secretHolder::setPass,
                this,
                new TestPanel()
        );

        this.add(passwordPanel);
//        JPanel regPanel = new RegistrationPanel(sr.userService(), sr.passwordService());
//        this.getContentPane().add(regPanel);

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
