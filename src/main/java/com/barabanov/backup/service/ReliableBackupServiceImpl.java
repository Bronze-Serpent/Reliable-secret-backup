package com.barabanov.backup.service;

import com.barabanov.backup.cloud.CloudService;
import com.barabanov.backup.cryptography.CryptoService;
import com.barabanov.backup.service.dto.FileInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.List;


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


    @Override
    public void saveFile(char[] keyStr, String filePath, Boolean isTracked)
    {
        String appFolderId = cloudService.findFolderWithName(APP_FOLDER_NAME, null);
        String storageId = cloudService.findFolderWithName(STORAGE_FOLDER_NAME, appFolderId);

        File file = new File(filePath);
        String fileName = file.getName();

        String uploadedFileId;
        try(InputStream encryptedIS = cryptoService.encrypt(keyStr, new FileInputStream(file)))
        {
            uploadedFileId = cloudService.uploadFile(storageId, fileName, encryptedIS);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // обновление файла с данными о файлах
        String masterFileId = cloudService.findFolderWithName(MASTER_FILE_NAME, appFolderId);
        InputStream inputStream = cloudService.downloadFile(masterFileId);

        StringBuilder masterFileText;
        try(BufferedReader masterFileBf = new BufferedReader(new InputStreamReader(inputStream)))
        {
            masterFileText = masterFileBf.lines()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        try(FileInputStream fileIS = new FileInputStream(file))
        {
            FileInfoDto fileInfoDto = FileInfoDto.builder()
                    .id(1L)
                    .diskId(uploadedFileId)
                    .name(fileName)
                    .md5(cryptoService.getMd5HashFor(fileIS))
                    .size(file.length() / 1024)
                    .createdDate(LocalDate.now())
                    .isTracked(isTracked)
                    .build();
            masterFileText.append(objectmapper.writeValueAsString(fileInfoDto));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // обновление файла с данными приложения (id файла)
//        String appInfoId = cloudService.findFolderWithName(APP_INFO_FILE_NAME, appFolderId);
        // загрузить файл, расшифровать его, передать jakson, чтобы получить объект, изменить объект, получить строку, зашифровать и обновить на диске
//        cloudService.update();
    }

    @Override
    public void deleteFile(Long fileId) {

    }

    @Override
    public void updateFile(Long fileId, String filePath) {

    }
}
