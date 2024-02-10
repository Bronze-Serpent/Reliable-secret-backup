package com.barabanov.backup.ui.file.check.components;

import com.barabanov.backup.service.dto.FileInfoDto;

import javax.swing.*;
import java.util.List;


public class CheckResultFrame extends JFrame
{
    public CheckResultFrame(List<FileInfoDto> fileInfoModifiedFiles)
    {
        super("Результаты проверки целостности файлов");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (fileInfoModifiedFiles.isEmpty())
            this.add(new SuccessCheckPanel(this));
        else
            this.add(new FailedFilesPanel(this, fileInfoModifiedFiles));
    }
}