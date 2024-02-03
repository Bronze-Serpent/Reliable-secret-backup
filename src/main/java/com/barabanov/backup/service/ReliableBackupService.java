package com.barabanov.backup.service;


import com.barabanov.backup.service.dto.FileInfoDto;

import java.util.List;

public interface ReliableBackupService
{
    void saveFile(char[] pass, String filePath, Boolean isTracked);

    void createAppDataFiles(char[] pass);

    List<FileInfoDto> showAllFilesInfo(char[] pass);

    public void downloadFile(Long id, String directory);

    // проверка файлов периодическая
}
