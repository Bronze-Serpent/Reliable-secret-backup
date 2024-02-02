package com.barabanov.backup.service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileInfoDto
{
    String name;
    Long id;
    String diskId;
    String md5;
    Boolean isTracked;
    Long size;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate createdDate;
}
