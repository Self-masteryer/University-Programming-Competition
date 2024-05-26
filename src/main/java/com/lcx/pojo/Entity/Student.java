package com.lcx.pojo.Entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Student {

    private int uid;
    private String name;
    private String school;

}
