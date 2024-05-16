package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private int id;
    private int uid;
    private String name;
    private String IDCard;
    private String group;
    private int zone;
    private int role;
}
