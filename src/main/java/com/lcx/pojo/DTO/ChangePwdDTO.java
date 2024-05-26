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
public class ChangePwdDTO {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.$@!%*?&])[A-Za-z\\d.$@!%*?&]{8,16}$")
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.$@!%*?&])[A-Za-z\\d.$@!%*?&]{8,16}$")
    private String newPassword;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.$@!%*?&])[A-Za-z\\d.$@!%*?&]{8,16}$")
    private String confirmPassword;

}
