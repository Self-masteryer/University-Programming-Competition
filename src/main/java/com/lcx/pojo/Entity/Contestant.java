package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contestant {

    private int id;
    private int uid;
    private String name;
    private String school;
    private String idCard;
    private String group;
    private String zone;

}
