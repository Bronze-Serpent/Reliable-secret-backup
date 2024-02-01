package com.barabanov.backup.service;


public interface ReliableBackupService
{
    void saveFile(char[] keyStr, String filePath, Boolean isTracked);

    void deleteFile(Long fileId);

    void updateFile(Long fileId, String filePath);

    // показ файлов, проверка их периодическая
}
