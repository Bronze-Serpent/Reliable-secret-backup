package com.barabanov.backup.cloud;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.function.Supplier;


@Configuration
public class GoogleDriveConfig
{

    @Bean
    public Supplier<Drive> googleDriveSupplier(
            @Value("${google.drive.credential.path:/credentials.json}") String credentialsFilePath,
            @Value("${google.drive.tokens.path:tokens/google}") String tokensDirPath,
            @Value("${google.drive.receiver.port:8888}") int receiverPort,
            @Value("${application.name:Reliable-secret-backup}") String applicationName,
            NetHttpTransport netHttpTransport,
            JsonFactory jsonFactory)
    {
        return () ->
        {
            try {
                Credential credentials = googleDriveCredentials(
                        credentialsFilePath,
                        tokensDirPath,
                        receiverPort,
                        netHttpTransport
                );
                return new Drive.Builder(netHttpTransport, jsonFactory, credentials)
                        .setApplicationName(applicationName)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // TODO: 25.01.2024 какую-нибудь ошибку тут, чтобы было понятно, что не удалось соединиться с гугл
        };
    }


    private Credential googleDriveCredentials(
            String credentialsFilePath,
            String tokensDirPath,
            int receiverPort,
            NetHttpTransport netHttpTransport)

            throws IOException
    {
        List<String> scopes =
                List.of(DriveScopes.DRIVE_METADATA_READONLY,
                        DriveScopes.DRIVE_FILE,
                        DriveScopes.DRIVE_METADATA,
                        DriveScopes.DRIVE_READONLY);

        // Load client secrets.
        InputStream in = GoogleDriveConfig.class.getResourceAsStream(credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(jsonFactory(), new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                netHttpTransport, jsonFactory(), clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirPath)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(receiverPort)
                .build();

        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
        // TODO: 24.01.2024 "user" непонятно зачем наверху
    }


    @Bean
    public JsonFactory jsonFactory()
    {
        return GsonFactory.getDefaultInstance();
    }


    @Bean
    public NetHttpTransport googleNetHttpTransport() throws GeneralSecurityException, IOException
    {
        return GoogleNetHttpTransport.newTrustedTransport();
    }
}
