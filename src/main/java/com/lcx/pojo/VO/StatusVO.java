package com.lcx.pojo.VO;

import com.lcx.pojo.DAO.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusVO {
    private String name;
    private String role;
    private String group;
    private String zone;
    private Status status;
}
