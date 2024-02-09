package com.barabanov.backup.service;

import com.barabanov.backup.service.dto.FileInfoDto;

import java.util.List;


public interface ReliableBackupService
{
    void authorizeInCloud();

    boolean isFileAreOnDisk();

    void createInitCloudElements(char[] pass);

    void saveFile(char[] pass, String filePath, Boolean isTracked);

    List<FileInfoDto> getFilesInfo(char[] pass, FileType fileType);

    void downloadFile(char[] pass, Long id, String directory);

    List<FileInfoDto> checkAllTrackedFiles(char[] pass);

    void changeTrackingPolicy(char[] pass, Long fileId);

    void deleteFile(char[] pass, Long fileId);
}
