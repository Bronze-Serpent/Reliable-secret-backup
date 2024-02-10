package com.barabanov.backup.ui.file.check.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class CloseFrameListener extends AbstractAction
{
    private final Window window;


    public CloseFrameListener(Window window)
    {
        this.window = window;
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.window.removeAll();
        this.window.dispose();
    }
}
