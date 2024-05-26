package com.lcx.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo {

    private String nickname;
    @URL
    private String avatar;
}
