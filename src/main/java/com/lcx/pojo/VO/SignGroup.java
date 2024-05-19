package com.lcx.pojo.VO;

import com.lcx.pojo.Entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignGroup {
    private int signNum;
    // 选手A
    private Student A;
    // 选手B
    private Student B;
}
