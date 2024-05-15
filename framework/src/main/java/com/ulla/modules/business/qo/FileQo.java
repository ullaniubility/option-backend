package com.ulla.modules.business.qo;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileQo {

    @ApiModelProperty("用户id")
    private String uid;

    @ApiModelProperty("用户证件照")
    private MultipartFile file;

}
