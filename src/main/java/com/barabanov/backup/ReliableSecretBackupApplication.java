package com.barabanov.backup;

import com.barabanov.backup.service.FileType;
import com.barabanov.backup.service.ReliableBackupService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


//@SpringBootApplication
public class ReliableSecretBackupApplication
{
	public static void main(String[] args)
	{
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ReliableSecretBackupApplication.class);
		// для использования awt
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);

		ReliableBackupService service = context.getBean(ReliableBackupService.class);

		char[] pass = "password".toCharArray();

		service.authorizeInCloud();
//		service.createInitCloudElements(pass);

//		service.saveFile(pass, "src/main/resources/test_1.txt", true);
//		service.saveFile(pass, "src/main/resources/test_2.txt", true);
//		service.saveFile(pass, "src/main/resources/pic_test.png", false);

		service.getFilesInfo(pass, FileType.ALL).forEach(System.out::println);

//		service.downloadFile(pass, 0L, "downloaded/");
//		service.downloadFile(pass, 1L, "downloaded/");
//		service.downloadFile(pass, 2L, "downloaded/");

		service.checkAllTrackedFiles(pass);
	}







	private static void emailTest()
	{
//		EmailServiceImpl bean = context.getBean(EmailServiceImpl.class);
//		try {
//			bean.sendHtmlEmail(
//					"pbarabanov04@gmail.com",
//					"Моя тема",
//					"<p>Текст <b>html</b></p>");
//		} catch (MessagingException e) {
//			throw new RuntimeException(e);
//		}
	}

}
