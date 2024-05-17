package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Integer id ;

    private String username ;

    private String password ;

    private String name ;

    private Integer rid ;

    private String avatar ;

    private Integer enabled ;

}