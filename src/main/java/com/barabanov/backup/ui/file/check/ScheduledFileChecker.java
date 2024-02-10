package com.barabanov.backup.ui.file.check;

import com.barabanov.backup.service.ReliableBackupService;
import com.barabanov.backup.service.dto.ChangeFileDto;
import com.barabanov.backup.service.dto.FileInfoDto;
import com.barabanov.backup.ui.file.check.components.CheckResultFrame;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;


@Component
@RequiredArgsConstructor
public class ScheduledFileChecker
{
    private final Supplier<char[]> passSupplier;
    private final ReliableBackupService backupService;


    @Async
    @Scheduled(fixedRateString = "${application.backup.file.checker.delay:60000}")
    public void checkAllFiles()
    {
        List<ChangeFileDto> fileInfoModifiedFiles = backupService.checkAllTrackedFiles(passSupplier.get());

        CheckResultFrame checkResultFrame = new CheckResultFrame(fileInfoModifiedFiles);
        checkResultFrame.pack();

        checkResultFrame.setVisible(true);
    }
}
