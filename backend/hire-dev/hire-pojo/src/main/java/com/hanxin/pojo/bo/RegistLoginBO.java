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
public class RegistLoginBO {

    @NotBlank(message = "email can't be null")
    private String email;
    @NotBlank(message = "code can't be null")
    private String code;


}
