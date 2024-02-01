package com.barabanov.backup.cloud.google;

import com.barabanov.backup.cloud.CloudService;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


@RequiredArgsConstructor
@Service
public class GoogleDriveCloudService implements CloudService
{

    private final Supplier<Drive> googleDriveSupplier;

    @Override
    public void MoveFileToFolder(String newFolderId, String fileId)
    {
        try
        {
            // Retrieve the existing parents to remove
            File file = googleDriveSupplier.get().files().get(fileId)
                    .setFields("parents")
                    .execute();
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }

            // Move the file to the new folder
            googleDriveSupplier.get().files().update(fileId, null)
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
            return googleDriveSupplier.get()
                    .files()
                    .get(fileId)
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

            File file = googleDriveSupplier.get().files().create(fileMetadata, inputStreamMediaContent)
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
            Drive drive = googleDriveSupplier.get();
            File file = drive.files().create(fileMetadata)
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


    @Override
    public String findFolderWithName(String name, String parentId)
    {
        try
        {
            Drive drive = googleDriveSupplier.get();
            List<File> fileList = drive.files().list()
                    .setQ("mimeType = 'application/vnd.google-apps.folder' and name = '" + name + "'")
                    .execute()
                    .getFiles();

            if (fileList.isEmpty())
                return null;
            else
                return fileList.get(0).getId();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String findFileFile(String name)
    {
        try
        {
            Drive drive = googleDriveSupplier.get();
            List<File> fileList = drive.files().list()
                    .setQ("name = '" + name + "'")
                    .execute()
                    .getFiles();

            if (fileList.isEmpty())
                return null;
            else
                return fileList.get(0).getId();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void authorize()
    {
        googleDriveSupplier.get();
    }


    @Override
    public void delete(String fileId)
    {
        try
        {
            googleDriveSupplier.get()
                    .files()
                    .delete(fileId)
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
    public void update(String fileId, String filepath)
    {
        Drive drive = googleDriveSupplier.get();
        try {
            File file = drive.files().get(fileId).execute();
            drive.files().update(fileId, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
