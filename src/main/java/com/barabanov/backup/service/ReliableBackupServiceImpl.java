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
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ReliableBackupServiceImpl implements ReliableBackupService
{
    @Value("${application.backup.app.folder.name}")
    private final String APP_FOLDER_NAME;

    @Value("${application.backup.storage.folder.name}")
    private final String STORAGE_FOLDER_NAME;

    @Value("${application.backup.master.file.name}")
    private final String MASTER_FILE_NAME;

    @Value("${application.backup.app.info.file.name}")
    private final String APP_INFO_FILE_NAME;


    private final CloudService cloudService;
    private final CryptoService cryptoService;
    private final ObjectMapper objectmapper;

    private String appDataCloudId;
    private String masterFileCloudId;
    private String appCloudFolderId;
    private String storageCloudFolderId;


    @Override
    public void saveFile(char[] pass, String filePath, Boolean isTracked)
    {
        File file = new File(filePath);
        String fileName = file.getName();

        String uploadedFileId;
        try(InputStream encryptedIS = cryptoService.encrypt(pass, new FileInputStream(file)))
        {
            uploadedFileId = cloudService.uploadFile(getStorageFolderId(), fileName, encryptedIS);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // обновление файлов с данными приложения
        StoredAppDataDto appDataDto = getObjectFromCloud(pass, getAppDataCloudId(), StoredAppDataDto.class);
        MasterFile storedFilesInfoList = getObjectFromCloud(pass, getMasterFileCloudId(), MasterFile.class);

        try(FileInputStream fileIS = new FileInputStream(file))
        {
            FileInfoDto fileInfoDto = FileInfoDto.builder()
                    .id(appDataDto.getCurrFileId())
                    .diskId(uploadedFileId)
                    .name(fileName)
                    .md5(cryptoService.getMd5HashFor(fileIS))
                    .size(file.length() / 1024)
                    .createdDate(LocalDate.now())
                    .isTracked(isTracked)
                    .build();

            storedFilesInfoList.getFilesInfo().add(fileInfoDto);
            appDataDto.setCurrFileId(appDataDto.getCurrFileId() + 1);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            System.out.println(objectmapper.writeValueAsString(appDataDto));
            cloudService.update(getAppDataCloudId(), new ByteArrayInputStream(objectmapper.writeValueAsString(appDataDto).getBytes()));
            cloudService.update(getMasterFileCloudId(), new ByteArrayInputStream(objectmapper.writeValueAsString(storedFilesInfoList).getBytes()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteFile(Long fileId)
    {
        InputStream encryptStream = cryptoService.encrypt("password".toCharArray(), new ByteArrayInputStream("{\"currFileId\":1}".getBytes()));
        InputStream decrypt = cryptoService.decrypt("password".toCharArray(), encryptStream);
        BufferedReader fileBf = new BufferedReader(new InputStreamReader(decrypt));
        try {
            System.out.println(fileBf.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAppDataFile(char[] pass)
    {
        try
        {
            String dataAsJson = objectmapper.writeValueAsString(new StoredAppDataDto(0L));
            createFile(pass, APP_INFO_FILE_NAME, dataAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void createMasterFile(char[] pass)
    {
        try
        {
            String dataAsJson = objectmapper.writeValueAsString(new MasterFile(Collections.emptyList()));
            createFile(pass, MASTER_FILE_NAME, dataAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private <T> T getObjectFromCloud(char[] pass, String cloudId, Class<T> objClazz)
    {
        try(BufferedReader fileBf = new BufferedReader(new InputStreamReader(cryptoService.decrypt(pass, cloudService.downloadFile(cloudId)))))
        {
            return objectmapper.readValue(fileBf.readLine(), objClazz);
        } catch (IOException e)
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
        if (appCloudFolderId == null)
            appCloudFolderId = cloudService.findFolderWithName(APP_FOLDER_NAME, null);

        return appCloudFolderId;
    }

    private String getStorageFolderId()
    {
        if (storageCloudFolderId == null)
            storageCloudFolderId = cloudService.findFolderWithName(STORAGE_FOLDER_NAME, getAppFolderId());

        return storageCloudFolderId;
    }

}
