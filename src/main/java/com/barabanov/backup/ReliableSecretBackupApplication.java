package com.barabanov.backup;

import com.barabanov.backup.email.EmailServiceImpl;
import jakarta.mail.MessagingException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileNotFoundException;


@SpringBootApplication
public class ReliableSecretBackupApplication
{
	public static void main(String[] args) throws FileNotFoundException
	{
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ReliableSecretBackupApplication.class);
		// для использования awt
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);

		EmailServiceImpl bean = context.getBean(EmailServiceImpl.class);
		try {
			bean.sendHtmlEmail(
					"pbarabanov04@gmail.com",
					"Мой тема",
					"<p>Текст <b>html</b></p>");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

//		CloudService cloudService = context.getBean(CloudService.class);
//		cloudService.testConnection();

//		String folderId = cloudService.createFolder("my folder", null);
//		String anotherFolderId = cloudService.createFolder("another folder", folderId);
//
//		String file1 = cloudService.uploadFile(folderId, "file1", new FileInputStream("src/main/resources/test.txt"));
//		String file1_1 = cloudService.uploadFile(anotherFolderId, "file1_1", new FileInputStream("src/main/resources/test.txt"));
//
//		cloudService.MoveFileToFolder(folderId, file1_1);
	}

}
