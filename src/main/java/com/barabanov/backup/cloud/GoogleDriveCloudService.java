package com.barabanov.backup.cloud;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;


@RequiredArgsConstructor
@Service
public class GoogleDriveCloudService implements CloudService
{

    private final Drive service;


    @Override
    public void MoveFileToFolder(String newFolderId, String fileId)
    {
        try
        {
            // Retrieve the existing parents to remove
            File file = service.files().get(fileId)
                    .setFields("parents")
                    .execute();
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }

            // Move the file to the new folder
            service.files().update(fileId, null)
                    .setAddParents(newFolderId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();
        }
        //        catch (GoogleJsonResponseException e)
        catch (IOException e)
        {
            System.err.println("Unable to move file: " + e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public InputStream downloadFile(String fileId)
    {
        try
        {
            return service.files().get(fileId)
                    .executeMediaAsInputStream();
        }
//        catch (GoogleJsonResponseException e)
//        {
//            // TODO(developer) - handle error appropriately
//            System.err.println("Unable to move file: " + e.getDetails());
//            throw new RuntimeException(e);
//        }
        catch (IOException e)
        {
            System.err.println("Unable to move file: " + e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public String uploadFile(String folderId, String fileName, InputStream dataIS)
    {
        // File's metadata.
        File fileMetadata = new File();
        fileMetadata.setParents(Collections.singletonList(folderId));
        fileMetadata.setName(fileName);
        fileMetadata.setMimeType("application/octet-stream"); // application/octet-stream - вроде необработанные бинарные данные

        try(dataIS)
        {
            // "text/plain" возможно, вместо null вообще нужно, чтобы был .file
            AbstractInputStreamContent inputStreamMediaContent = new InputStreamContent(null, dataIS);

            File file = service.files().create(fileMetadata, inputStreamMediaContent)
                    .setFields("id")
                    .execute();
            return file.getId();
        }
        //        catch (GoogleJsonResponseException e)
        catch (IOException e)
        {
            System.err.println("Unable to move file: " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createFolder(String name, String parentId)
    {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(Collections.singletonList(parentId));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        try
        {
            File file = service.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            return file.getId();
        } catch (IOException e)
        {
            System.err.println("Unable to move file: " + e);
            throw new RuntimeException(e);
        }
//        catch (GoogleJsonResponseException e)
    }
}
