package com.barabanov.backup.ui.listener;

import com.barabanov.backup.service.FileType;
import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.service.dto.FileInfoDto;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


@RequiredArgsConstructor
public class ShowFilesListener extends AbstractAction
{
    private final Component whereToShow;
    private final ReliableBackupService backupService;
    private final FileType fileType;
    private final Supplier<char[]> passSupplier;


    @Override
    public void actionPerformed(ActionEvent e)
    {
        List<FileInfoDto> filesInfo = backupService.getFilesInfo(passSupplier.get(), fileType);

        String[] columnNames = {"Id", "Filename", "Size (Kb)", "Date", "MD5", "Is tracked"};
        List<String[]> tableData = new ArrayList<>();

        for (FileInfoDto fileInfoDto : filesInfo)
        {
            tableData.add(new String[]{
                    fileInfoDto.getId().toString(),
                    fileInfoDto.getName(),
                    fileInfoDto.getSize().toString(),
                    fileInfoDto.getCreatedDate().toString(),
                    fileInfoDto.getMd5(),
                    fileInfoDto.getIsTracked().toString()}
            );
        }
        JTable filesTable = new JTable(tableData.toArray(String[][]::new), columnNames);
        filesTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(filesTable);

        JOptionPane.showMessageDialog(whereToShow, scrollPane);
    }
}
