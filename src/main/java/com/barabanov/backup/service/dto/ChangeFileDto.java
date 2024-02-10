package com.barabanov.backup.service.dto;

import com.barabanov.backup.service.ChangeType;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeFileDto
{
    ChangeType changeType;
    FileInfoDto fileInfoDto;
}
