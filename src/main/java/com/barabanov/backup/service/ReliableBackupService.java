package com.barabanov.backup.service;


public interface ReliableBackupService
{
    void saveFile(char[] keyStr, String filePath, Boolean isTracked);

    void deleteFile(Long fileId);

    void createAppDataFile(char[] pass);

    void createMasterFile(char[] pass);

    // показ файлов, проверка их периодическая
}
