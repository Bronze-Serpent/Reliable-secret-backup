package com.barabanov.backup.ui.file.check.components;

import com.barabanov.backup.service.dto.ChangeFileDto;
import com.barabanov.backup.service.dto.FileInfoDto;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;


public class FailedFilesPanel extends JPanel
{
    public FailedFilesPanel(Window parentWindow, List<ChangeFileDto> fileInfoModifiedFiles)
    {
        super(new MigLayout("insets 12",
                "[]",
                "[]20[]"));

        String[] columnNames = {"Id", "Filename", "Size (Kb)", "Date", "MD5", "Change type"};
        List<String[]> modifiedFiles = new ArrayList<>();

        for (ChangeFileDto changeFileDto : fileInfoModifiedFiles)
        {
            FileInfoDto fileInfoDto = changeFileDto.getFileInfoDto();
            modifiedFiles.add(new String[]{
                    fileInfoDto.getId().toString(),
                    fileInfoDto.getName(),
                    fileInfoDto.getSize().toString(),
                    fileInfoDto.getCreatedDate().toString(),
                    fileInfoDto.getMd5(),
                    changeFileDto.getChangeType().toString()
                    }
            );
        }
        JTable failedFilesTbl = new JTable(modifiedFiles.toArray(String[][]::new), columnNames);
        failedFilesTbl.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(failedFilesTbl);

        JButton okBtn = new JButton("Ок");
        okBtn.addActionListener(new CloseFrameListener(parentWindow));

        this.add(scrollPane, "wrap");
        this.add(okBtn, "center");
    }
}
