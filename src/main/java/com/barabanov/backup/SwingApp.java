package com.barabanov.backup;

import com.barabanov.backup.ui.window.ReliableBackupFrame;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;


@SpringBootApplication
public class SwingApp extends ReliableBackupFrame
{
    public static void main(String[] args)
    {

        var contextBuilder = new SpringApplicationBuilder(SwingApp.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        EventQueue.invokeLater(() ->
        {
            var swingApp = contextBuilder.getBean(SwingApp.class);
            swingApp.setVisible(true);
        });
    }

}