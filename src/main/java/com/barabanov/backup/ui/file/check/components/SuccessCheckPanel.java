package com.barabanov.backup.ui.file.check.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


public class SuccessCheckPanel extends JPanel
{
    public SuccessCheckPanel(Window parentWindow)
    {
        super(new MigLayout("insets 12",
                "[]",
                "[]20[]"));

        JLabel filesSuccessLbl = new JLabel("Все файлы успешно прошли проверку");
        JButton okBtn = new JButton("Ок");
        okBtn.addActionListener(new CloseFrameListener(parentWindow));

        this.add(filesSuccessLbl, "wrap");
        this.add(okBtn, "center");
    }
}
