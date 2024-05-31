package com.lcx.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusVO {
    private String name;
    private String role;
    private String group;
    private String zone;
    private String status;
    private LocalDateTime onlineTime;
}
