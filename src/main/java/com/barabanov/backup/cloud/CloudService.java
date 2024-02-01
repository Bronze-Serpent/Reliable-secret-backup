package com.barabanov.backup.cloud;

import java.io.InputStream;


public interface CloudService
{
    void MoveFileToFolder(String newFolderId, String fileId);

    InputStream downloadFile(String fileId);

    String uploadFile(String folderId, String fileName, InputStream dataIS);

    String createFolder(String name, String parentId);

    void authorize();

    void delete(String fileId);
}
