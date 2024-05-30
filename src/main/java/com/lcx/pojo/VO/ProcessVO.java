package com.lcx.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessVO {

    private String group;// 组别
    private String zone;// 赛区
    private String process;// 进程
    private String step;// 步骤

}
