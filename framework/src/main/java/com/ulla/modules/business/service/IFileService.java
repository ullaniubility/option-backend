package com.ulla.modules.business.service;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.FileMo;

public interface IFileService extends IService<FileMo> {

    ResultMessageVo setPortrait(MultipartFile file, Long uid);

    ResultMessageVo upload(MultipartFile file);
}
