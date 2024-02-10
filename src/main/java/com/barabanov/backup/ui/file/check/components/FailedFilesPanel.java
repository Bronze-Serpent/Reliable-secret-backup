package com.barabanov.backup.ui.file.check.components;

import com.barabanov.backup.service.dto.FileInfoDto;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class FailedFilesPanel extends JPanel
{
    public FailedFilesPanel(Window parentWindow, java.util.List<FileInfoDto> fileInfoModifiedFiles)
    {
        super(new MigLayout("insets 12",
                "[]",
                "[]20[]"));

        String[] columnNames = {"Id", "Filename", "Size (Kb)", "Date", "MD5"};
        List<String[]> modifiedFiles = new ArrayList<>();

        for (FileInfoDto fileInfoDto : fileInfoModifiedFiles)
        {
            modifiedFiles.add(new String[]{
                    fileInfoDto.getId().toString(),
                    fileInfoDto.getName(),
                    fileInfoDto.getSize().toString(),
                    fileInfoDto.getCreatedDate().toString(),
                    fileInfoDto.getMd5()}
            );
        }
        JTable failedFilesTbl = new JTable(modifiedFiles.toArray(String[][]::new), columnNames);
        JButton okBtn = new JButton("Ок");
        okBtn.addActionListener(new CloseFrameListener(parentWindow));

        this.add(failedFilesTbl, "wrap");
        this.add(okBtn, "center");
    }
}
