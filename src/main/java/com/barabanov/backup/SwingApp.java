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
public class SwingApp
{
    public static void main(String[] args)
    {
        var contextBuilder = new SpringApplicationBuilder(SwingApp.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        SecretHolder secretHolder = contextBuilder.getBean(SecretHolder.class);
        ReliableBackupService backupService = contextBuilder.getBean(ReliableBackupService.class);

        MainFrame mainSwingFrame = new MainFrame(backupService, secretHolder);
        mainSwingFrame.setVisible(true);
    }

}