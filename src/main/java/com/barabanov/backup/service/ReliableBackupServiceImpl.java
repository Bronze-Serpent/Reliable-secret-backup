package com.barabanov.backup.service;

import com.barabanov.backup.cloud.CloudService;
import com.barabanov.backup.cryptography.CryptoService;
import com.barabanov.backup.service.dto.FileInfoDto;
import com.barabanov.backup.service.dto.MasterFile;
import com.barabanov.backup.service.dto.StoredAppDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.barabanov.backup.service.FileType.ALL;
import static com.barabanov.backup.service.FileType.TRACKED;


@RequiredArgsConstructor
@Service
public class ReliableBackupServiceImpl implements ReliableBackupService
{
    @Value("${application.backup.app.folder.name:Reliable-backup}")
    private final String APP_FOLDER_NAME;

    @Value("${application.backup.storage.folder.name:Files}")
    private final String STORAGE_FOLDER_NAME;

    @Value("${application.backup.master.file.name:master-file}")
    private final String MASTER_FILE_NAME;

    @Value("${application.backup.app.info.file.name:app-info}")
    private final String APP_INFO_FILE_NAME;

    private final CloudService cloudService;
    private final CryptoService cryptoService;
    private final ObjectMapper objectmapper;

    private String appDataCloudId;
    private String masterFileCloudId;
    private String appFolderCloudId;
    private String storageCloudFolderId;


    @Override
    public void authorizeInCloud()
    {
        cloudService.authorize();
    }


    @Override
    public boolean isFileAreOnDisk()
    {
        if (getAppFolderId() == null)
            return false;
        if (getStorageFolderId() == null ||
                getMasterFileCloudId() == null ||
                getAppDataCloudId() == null)
        {
            cloudService.delete(getAppFolderId());
            return false;
        }
        else
            return true;
    }


    @Override
    public void saveFile(char[] pass, String filePath, Boolean isTracked)
    {
        File file = new File(filePath);
        String uploadedFileId = uploadFile(pass, file);

        // обновление файлов с данными приложения
        StoredAppDataDto appDataDto = getObjectFromCloud(pass, getAppDataCloudId(), StoredAppDataDto.class);
        MasterFile masterFile = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);

        FileInfoDto fileInfoDto = createFileInfo(file, appDataDto.getCurrFileId(), uploadedFileId, isTracked);
        masterFile.getFilesInfo()
                .add(fileInfoDto);
        appDataDto.setCurrFileId(appDataDto.getCurrFileId() + 1);

        updateAppFiles(pass, appDataDto, masterFile);
    }


    @Override
    public void createInitCloudElements(char[] pass)
    {
        this.appFolderCloudId = cloudService.createFolder(APP_FOLDER_NAME, null);
        this.storageCloudFolderId = cloudService.createFolder(STORAGE_FOLDER_NAME, appFolderCloudId);
        try
        {
            String appDataAsJson = objectmapper.writeValueAsString(new StoredAppDataDto(0L));
            createFile(pass, APP_INFO_FILE_NAME, appDataAsJson);

            String masterFileAsJson = objectmapper.writeValueAsString(new MasterFile(Collections.emptyList()));
            createFile(pass, MASTER_FILE_NAME, masterFileAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<FileInfoDto> getFilesInfo(char[] pass, FileType fileType)
    {
        MasterFile masterFile = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);
        List<FileInfoDto> allFileInfo = masterFile.getFilesInfo();

        if (fileType == ALL)
            return allFileInfo;
        if (fileType == TRACKED)
            return allFileInfo.stream()
                    .filter(FileInfoDto::getIsTracked)
                    .collect(Collectors.toList());
        else
            return allFileInfo.stream()
                    .filter(fileInfo-> !fileInfo.getIsTracked())
                    .collect(Collectors.toList());
    }


    @Override
    public void downloadFile(char[] pass, Long fileId, String fileDirectory)
    {
        MasterFile masterFile = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);
        List<FileInfoDto> idFileInfoList = masterFile.getFilesInfo().stream()
                .filter(fileInfoDto -> fileInfoDto.getId().equals(fileId))
                .toList();
        if (idFileInfoList.size() == 1)
        {
            FileInfoDto fileInfo = idFileInfoList.get(0);
            InputStream decryptedIS = cryptoService.decrypt(pass, cloudService.downloadFile(fileInfo.getCloudId()));
            try
            {
                File targetFile = new File(fileDirectory + "\\" + fileInfo.getName());
                Files.createDirectories(targetFile.getAbsoluteFile().toPath().getParent());

                Files.copy(decryptedIS, targetFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // считываю Master-file и файлы по одному считываю по id,
    // их расшифровываю, вычисляю хэш и сравниваю с тем, что хранится в master-file
    @Override
    public List<FileInfoDto> checkAllTrackedFiles(char[] pass)
    {
        MasterFile masterFile = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);
        List<FileInfoDto> notVerified = new ArrayList<>();

        for (FileInfoDto fileInfoDto : masterFile.getFilesInfo())
        {
            if (fileInfoDto.getIsTracked())
            {
                String cloudId = fileInfoDto.getCloudId();
                try(InputStream fileIO = cryptoService.decrypt(pass, cloudService.downloadFile(cloudId)))
                {
                    String newMd5Hash = cryptoService.getMd5HashFor(fileIO);

                    if(!newMd5Hash.equals(fileInfoDto.getMd5()))
                        notVerified.add(fileInfoDto);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        return notVerified;
    }


    @Override
    public void changeTrackingPolicy(char[] pass, Long fileId)
    {
        MasterFile masterFile = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);

        for (FileInfoDto fileInfoDto : masterFile.getFilesInfo())
        {
            if (fileInfoDto.getId().equals(fileId))
                fileInfoDto.setIsTracked(!fileInfoDto.getIsTracked());
        }

        updateAppFiles(pass, masterFile);
    }


    @Override
    public void deleteFile(char[] pass, Long fileId)
    {
        MasterFile masterFile = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);

        Iterator<FileInfoDto> dtoIterator = masterFile.getFilesInfo().iterator();
        while (dtoIterator.hasNext())
        {
            FileInfoDto fileInfoDto = dtoIterator.next();
            if (fileInfoDto.getId().equals(fileId))
            {
                cloudService.delete(fileInfoDto.getCloudId());
                dtoIterator.remove();
                break;
            }
        }

        updateAppFiles(pass, masterFile);
    }


    private void updateAppFiles(char[] pass, StoredAppDataDto appDataDto, MasterFile masterFile)
    {
        try
        {
            cloudService.update(getAppDataCloudId(), cryptoService.encrypt(pass, new ByteArrayInputStream(objectmapper.writeValueAsString(appDataDto).getBytes())));
            cloudService.update(getMasterFileCloudId(), cryptoService.encrypt(pass, new ByteArrayInputStream(objectmapper.writeValueAsString(masterFile).getBytes())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateAppFiles(char[] pass, MasterFile masterFile)
    {
        try
        {
            cloudService.update(getMasterFileCloudId(), cryptoService.encrypt(pass, new ByteArrayInputStream(objectmapper.writeValueAsString(masterFile).getBytes())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private FileInfoDto createFileInfo(File file, Long currId, String cloudId, Boolean isTracked)
    {
        try(FileInputStream fileIS = new FileInputStream(file))
        {
            return FileInfoDto.builder()
                    .id(currId)
                    .cloudId(cloudId)
                    .name(file.getName())
                    .md5(cryptoService.getMd5HashFor(fileIS))
                    .size(Math.round(file.length() * 1.0 / 1024))
                    .createdDate(LocalDate.now())
                    .isTracked(isTracked)
                    .build();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private <T> T getObjectFromCloud(char[] pass, String cloudId, Class<T> objClazz)
    {
        try(BufferedReader fileBf = new BufferedReader(new InputStreamReader(cryptoService.decrypt(pass, cloudService.downloadFile(cloudId)))))
        {
            // TODO: 03.02.2024 тут одной строкой?
            return objectmapper.readValue(fileBf.readLine(), objClazz);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String uploadFile(char[] pass, File file)
    {
        try(InputStream encryptedIS = cryptoService.encrypt(pass, new FileInputStream(file)))
        {
            return cloudService.uploadFile(getStorageFolderId(), file.getName(), encryptedIS);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void createFile(char[] pass, String fileName, String dataAsJson)
    {
        try(InputStream encryptedStream = cryptoService.encrypt(pass, new ByteArrayInputStream(dataAsJson.getBytes(StandardCharsets.UTF_8))))
        {
            cloudService.uploadFile(getAppFolderId(), fileName, encryptedStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAppDataCloudId()
    {
        if (appDataCloudId == null)
            appDataCloudId = cloudService.findFileCloudId(APP_INFO_FILE_NAME);

        return appDataCloudId;
    }

    private String getMasterFileCloudId()
    {
        if (masterFileCloudId == null)
            masterFileCloudId = cloudService.findFileCloudId(MASTER_FILE_NAME);

        return masterFileCloudId;
    }

    private String getAppFolderId()
    {
        if (appFolderCloudId == null)
            appFolderCloudId = cloudService.findFolderWithName(APP_FOLDER_NAME, null);

        return appFolderCloudId;
    }

    private String getStorageFolderId()
    {
        if (storageCloudFolderId == null)
            storageCloudFolderId = cloudService.findFolderWithName(STORAGE_FOLDER_NAME, getAppFolderId());

        return storageCloudFolderId;
    }

}
