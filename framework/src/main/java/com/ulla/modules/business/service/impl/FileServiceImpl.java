package com.ulla.modules.business.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.AliyunClient;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.SysConfigConstant;
import com.ulla.constant.UserConstant;
import com.ulla.modules.admin.mapper.SysConfigMapper;
import com.ulla.modules.business.mapper.FileMapper;
import com.ulla.modules.business.mo.FileMo;
import com.ulla.modules.business.service.IFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileServiceImpl extends ServiceImpl<FileMapper, FileMo> implements IFileService {

    final SysConfigMapper sysConfigMapper;

    final FileMapper fileMapper;

    @Override
    public ResultMessageVo setPortrait(MultipartFile file, Long uid) {
        Map<String, String> map = new HashMap<>();
        try {
            AliyunClient aliyunClient = getAliyunClient();
            String url =
                aliyunClient.uploadObject2OSS(new ByteArrayInputStream(file.getBytes()), file.getOriginalFilename());
            map.put("url", UserConstant.HTTPS + url);
            FileMo fileMo = fileMapper.selectByUId(uid);
            if (ObjectUtils.isEmpty(fileMo)) {
                FileMo fileMo1 = new FileMo();
                fileMo1.setFileUrl(UserConstant.HTTPS + url);
                fileMo1.setUid(uid);
                save(fileMo1);
            } else {
                fileMo.setFileUrl(UserConstant.HTTPS + url);
                fileMapper.updateById(fileMo);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return ResultUtil.data(map);
    }

    @Override
    public ResultMessageVo upload(MultipartFile file) {
        FileMo fileMo1 = new FileMo();
        Map<String, String> map = new HashMap<>();
        try {
            AliyunClient aliyunClient = getAliyunClient();
            String url =
                aliyunClient.uploadObject2OSS(new ByteArrayInputStream(file.getBytes()), file.getOriginalFilename());
            map.put("url", UserConstant.HTTPS + url);
            fileMo1.setFileUrl(UserConstant.HTTPS + url);
            save(fileMo1);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return ResultUtil.data(map);
    }

    /**
     * 从系统设置中获取阿里云*
     *
     * @return AliyunClient AliyunClient
     */
    public AliyunClient getAliyunClient() {
        final String folder = sysConfigMapper.selectByKey(SysConfigConstant.FOLDER);
        final String access_key_id = sysConfigMapper.selectByKey(SysConfigConstant.ACCESS_KEY_ID);
        final String bucket = sysConfigMapper.selectByKey(SysConfigConstant.Bucket);
        final String access_key_secret = sysConfigMapper.selectByKey(SysConfigConstant.ACCESS_KEY_SECRET);
        final String endpoint = sysConfigMapper.selectByKey(SysConfigConstant.ENDPOINT);
        return new AliyunClient(endpoint, access_key_id, access_key_secret, bucket, folder);
    }

}
