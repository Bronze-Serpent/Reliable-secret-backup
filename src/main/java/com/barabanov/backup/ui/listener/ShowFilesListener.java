package com.barabanov.backup.ui.listener;

import com.barabanov.backup.service.FileType;
import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.service.dto.FileInfoDto;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


@RequiredArgsConstructor
public class ShowFilesListener extends AbstractAction
{
    private final Component whereToShow;
    private final ReliableBackupService backupService;
    private final FileType fileType;
    private final char[] pass;


    @Override
    public void actionPerformed(ActionEvent e)
    {
        List<FileInfoDto> filesInfo = backupService.getFilesInfo(pass, fileType);

        StringBuilder sb = new StringBuilder();
        for (FileInfoDto fileInfoDto : filesInfo)
        {
            sb.append(String.format("%3d - %40s - %7d - %10s - md5: %32s - is tracked: %5s",
                    fileInfoDto.getId(),
                    fileInfoDto.getName(),
                    fileInfoDto.getSize(),
                    fileInfoDto.getCreatedDate(),
                    fileInfoDto.getMd5(),
                    fileInfoDto.getIsTracked().toString()
                    ));
        }

        JOptionPane.showMessageDialog(whereToShow, sb.toString());
    }
}
