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
    private int sid;
    private String name;
    private String idCard;
    private String group;
    private String zone;

}
