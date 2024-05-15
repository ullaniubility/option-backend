package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.LearningMaterialsMo;
import com.ulla.modules.business.qo.LearningMaterialsQo;
import com.ulla.modules.business.service.ILearningMaterialsService;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "学习资料")
@RestController
@RequestMapping("/learningMaterials")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LearningMaterialsController {

    final ILearningMaterialsService learningMaterialsService;

    /**
     * 学习资料新增
     */
    @PostMapping("/save")
    @ApiOperation(value = "新增学习资料", notes = "新增学习资料")
    public ResultMessageVo save(@RequestBody LearningMaterialsMo learningMaterialsMo) {
        if (learningMaterialsMo == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (StrUtil.isEmpty(learningMaterialsMo.getTitle()) || StrUtil.isEmpty(learningMaterialsMo.getVideoUrl())
            || ObjectUtils.isEmpty(learningMaterialsMo.getStatusType())
            || ObjectUtils.isEmpty(learningMaterialsMo.getSort())
            || ObjectUtils.isEmpty(learningMaterialsMo.getVideoSize())
            || ObjectUtils.isEmpty(learningMaterialsMo.getVideoPicture())) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        if (this.learningMaterialsService.getOne(Wrappers.<LearningMaterialsMo>lambdaQuery()
            .eq(LearningMaterialsMo::getTitle, learningMaterialsMo.getTitle()), false) != null) {
            return ResultUtil.error(4002, "Title repetition");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        learningMaterialsMo.setTitle(learningMaterialsMo.getTitle().trim());
        learningMaterialsMo.setVideoUrl(learningMaterialsMo.getVideoUrl().trim());
        if (learningMaterialsMo.getVideoTime() == null) {
            learningMaterialsMo.setVideoTime(1F);
        }
        learningMaterialsMo.setVideoSize(learningMaterialsMo.getVideoSize());
        learningMaterialsMo.setVideoPicture(learningMaterialsMo.getVideoPicture());
        learningMaterialsMo.setStatusType(learningMaterialsMo.getStatusType());
        learningMaterialsMo.setSort(learningMaterialsMo.getSort());
        learningMaterialsMo.setCreateTime(System.currentTimeMillis());
        learningMaterialsMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.learningMaterialsService.save(learningMaterialsMo));
    }

    /**
     * 学习资料编辑
     */
    @PostMapping("/update/{id}")
    @ApiOperation(value = "编辑学习资料", notes = "编辑学习资料")
    public ResultMessageVo update(@PathVariable Long id, @RequestBody LearningMaterialsMo learningMaterialsMo) {
        if (learningMaterialsMo == null || id == null || !id.equals(learningMaterialsMo.getId())) {
            return ResultUtil.error(4002, "Parameter error");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        if (StrUtil.isNotEmpty(learningMaterialsMo.getTitle())) {
            learningMaterialsMo.setTitle(learningMaterialsMo.getTitle().trim());
        }
        if (StrUtil.isNotEmpty(learningMaterialsMo.getVideoUrl())) {
            learningMaterialsMo.setVideoUrl(learningMaterialsMo.getVideoUrl().trim());
        }
        if (ObjectUtils.isNotEmpty(learningMaterialsMo.getVideoSize())) {
            learningMaterialsMo.setVideoSize(learningMaterialsMo.getVideoSize());
        }
        if (ObjectUtils.isNotEmpty(learningMaterialsMo.getVideoTime())) {
            learningMaterialsMo.setVideoTime(learningMaterialsMo.getVideoTime());
        }
        if (StrUtil.isNotEmpty(learningMaterialsMo.getVideoPicture())) {
            learningMaterialsMo.setVideoPicture(learningMaterialsMo.getVideoPicture().trim());
        }
        if (ObjectUtils.isNotEmpty(learningMaterialsMo.getStatusType())) {
            learningMaterialsMo.setStatusType(learningMaterialsMo.getStatusType());
        }
        if (ObjectUtils.isNotEmpty(learningMaterialsMo.getSort())) {
            learningMaterialsMo.setSort(learningMaterialsMo.getSort());
        }
        learningMaterialsMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.learningMaterialsService.updateById(learningMaterialsMo));
    }

    /**
     * 学习资料删除
     */
    @ApiOperation(value = "根据主键删除学习资料单条数据", notes = "根据id删除")
    @PostMapping("/deleteById")
    public ResultMessageVo removeById(Integer id) {
        return ResultUtil.data(this.learningMaterialsService.removeById(id));
    }

    /**
     * 学习资料分页列表
     */
    @PostMapping("/page")
    @ApiOperation(value = "学习资料分页列表", notes = "学习资料分页列表")
    public ResultMessageVo<IPage<LearningMaterialsQo>> pageList(@RequestBody LearningMaterialsQo qo) {
        Page<LearningMaterialsQo> page = new Page<>(qo.getPage(), qo.getLimit());
        IPage<LearningMaterialsQo> learningMaterialsQoIPage = learningMaterialsService.pageList(page, qo);
        if (ObjectUtils.isEmpty(learningMaterialsQoIPage)) {
            return null;
        }
        return ResultUtil.data(learningMaterialsQoIPage);
    }

}
