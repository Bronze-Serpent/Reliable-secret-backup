package com.barabanov.backup.service;

import com.barabanov.backup.service.dto.FileInfoDto;

import java.util.List;


public interface ReliableBackupService
{
    void authorizeInCloud();

    void createInitCloudElements(char[] pass);

    void saveFile(char[] pass, String filePath, Boolean isTracked);

    List<FileInfoDto> showAllFilesInfo(char[] pass);

    void downloadFile(char[] pass, Long id, String directory);

    // проверка файлов периодическая
}
