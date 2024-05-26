package com.lcx.pojo.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetAccountDTO {

    //  4到16位（只能由字母，数字，下划线，减号）
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$")
    private String username;

    // 8~16个字符，至少1个大写字母，1个小写字母，1个数字和1个特殊字符
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.$@!%*?&])[A-Za-z\\d.$@!%*?&]{8,16}$")
    private String password;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.$@!%*?&])[A-Za-z\\d.$@!%*?&]{8,16}$")
    private String confirmPassword;

}
