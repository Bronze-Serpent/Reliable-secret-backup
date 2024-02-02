package com.barabanov.backup.cloud;

import java.io.InputStream;


public interface CloudService
{
    void authorize();

    InputStream downloadFile(String cloudId);

    String uploadFile(String cloudFolderId, String fileName, InputStream dataIS);

    void delete(String cloudId);

    void update(String cloudId, InputStream is);

    void moveFileToFolder(String folderCloudId, String fileCloudId);

    String createFolder(String name, String parentCloudId);

    String findFolderWithName(String name, String parentCloudId);

    String findFileCloudId(String name);

}
