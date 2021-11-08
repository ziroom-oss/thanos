package com.ziroom.qa.quality.defende.provider.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeVo {

    private LocalDateTime start;
    private LocalDateTime end;

}
