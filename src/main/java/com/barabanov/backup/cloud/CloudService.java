package com.barabanov.backup.cloud;

import java.io.InputStream;


public interface CloudService
{
    void authorize();

    InputStream downloadFile(String fileId);

    String uploadFile(String folderId, String fileName, InputStream dataIS);

    void delete(String fileId);

    void update(String fileId, String filepath);

    void MoveFileToFolder(String newFolderId, String fileId);

    String createFolder(String name, String parentId);

    String findFolderWithName(String name, String parentId);

    String findFileFile(String name);

}
