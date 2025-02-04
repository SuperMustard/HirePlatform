package com.hanxin.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminBO {

    @NotBlank(message = "uername can't be null")
    private String username;
    @NotBlank(message = "password can't be null")
    private String password;


}
