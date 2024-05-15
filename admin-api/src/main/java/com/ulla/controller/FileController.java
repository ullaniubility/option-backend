package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.business.service.IFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "用户证件照")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileController {

    final IFileService fileService;

    final UserMapper userMapper;

    /**
     * 用户上传证件照
     */
    @ApiOperation("设置证件照")
    @PostMapping(value = "/upload")
    public ResultMessageVo upload(MultipartFile file) {
        return fileService.upload(file);
    }

}
