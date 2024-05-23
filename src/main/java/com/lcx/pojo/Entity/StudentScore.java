package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentScore {
    private int id;
    private String school;
    private String name;
    private String idCard;
    private int session;
    private float score;
    private String prize;
}
