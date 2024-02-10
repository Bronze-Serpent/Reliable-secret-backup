package com.barabanov.backup;

import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.ui.SecretHolder;
import com.barabanov.backup.ui.MainFrame;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SwingApp extends MainFrame
{
    public SwingApp(ReliableBackupService backupService, SecretHolder secretHolder)
    {
        super(backupService, secretHolder);
    }

    public static void main(String[] args)
    {
        var contextBuilder = new SpringApplicationBuilder(SwingApp.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        var swingApp = contextBuilder.getBean(SwingApp.class);
        swingApp.setVisible(true);
    }

}