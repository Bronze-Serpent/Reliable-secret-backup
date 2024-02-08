package com.barabanov.backup.ui.window;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;


public class PasswordPanel extends JPanel
{

    public PasswordPanel(Consumer<char[]> passConsumer,
                         Container parentContainer,
                         JPanel toDisplayPanel)
    {
        super(new MigLayout());

        JLabel registerLbl = new JLabel("Укажите пароль. Он будет использоваться для шифрования");
        JLabel passLbl = new JLabel("Пароль: ");
        JTextField passTextField = new JTextField(25);

        JButton passSubmitBtn = new JButton("ОК");
        passSubmitBtn.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                passConsumer.accept(passTextField.getText().toCharArray());

                parentContainer.remove(PasswordPanel.this);
                parentContainer.add(toDisplayPanel);
                parentContainer.repaint();
            }
        });

        this.add(registerLbl, "span, center, dock north");
        this.add(passLbl);
        this.add(passTextField, "wrap");
        this.add(passSubmitBtn, "dock south");
    }
}
