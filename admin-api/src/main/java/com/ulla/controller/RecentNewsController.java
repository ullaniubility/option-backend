package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.RecentNewsMo;
import com.ulla.modules.business.qo.RecentNewsQo;
import com.ulla.modules.business.service.IRecentNewsService;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "最新动态")
@RestController
@RequestMapping("/recentNews")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecentNewsController {
    final IRecentNewsService recentNewsService;

    /**
     * 最新动态新增
     */
    @PostMapping("/save")
    @ApiOperation(value = "新增最新动态", notes = "新增最新动态")
    public ResultMessageVo save(@RequestBody RecentNewsMo recentNewsMo) {
        if (recentNewsMo == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (StrUtil.isEmpty(recentNewsMo.getTitle()) || StrUtil.isEmpty(recentNewsMo.getVideoUrl())
            || ObjectUtils.isEmpty(recentNewsMo.getStatusType()) || ObjectUtils.isEmpty(recentNewsMo.getSort())
            || ObjectUtils.isEmpty(recentNewsMo.getVideoSize())
            || ObjectUtils.isEmpty(recentNewsMo.getVideoPicture())) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        if (this.recentNewsService.getOne(
            Wrappers.<RecentNewsMo>lambdaQuery().eq(RecentNewsMo::getTitle, recentNewsMo.getTitle()), false) != null) {
            return ResultUtil.error(4002, "Title repetition");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        recentNewsMo.setTitle(recentNewsMo.getTitle().trim());
        recentNewsMo.setVideoUrl(recentNewsMo.getVideoUrl().trim());
        if (recentNewsMo.getVideoTime() == null) {
            recentNewsMo.setVideoTime(1F);
        }
        recentNewsMo.setVideoSize(recentNewsMo.getVideoSize());
        recentNewsMo.setVideoPicture(recentNewsMo.getVideoPicture());
        recentNewsMo.setStatusType(recentNewsMo.getStatusType());
        recentNewsMo.setSort(recentNewsMo.getSort());
        recentNewsMo.setCreateTime(System.currentTimeMillis());
        recentNewsMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.recentNewsService.save(recentNewsMo));
    }

    /**
     * 最新动态编辑
     */
    @PostMapping("/update/{id}")
    @ApiOperation(value = "编辑学习资料", notes = "编辑学习资料")
    public ResultMessageVo update(@PathVariable Long id, @RequestBody RecentNewsMo recentNewsMo) {
        if (recentNewsMo == null || id == null || !id.equals(recentNewsMo.getId())) {
            return ResultUtil.error(4002, "Parameter error");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        if (StrUtil.isNotEmpty(recentNewsMo.getTitle())) {
            recentNewsMo.setTitle(recentNewsMo.getTitle().trim());
        }
        if (StrUtil.isNotEmpty(recentNewsMo.getVideoUrl())) {
            recentNewsMo.setVideoUrl(recentNewsMo.getVideoUrl().trim());
        }
        if (ObjectUtils.isNotEmpty(recentNewsMo.getVideoSize())) {
            recentNewsMo.setVideoSize(recentNewsMo.getVideoSize());
        }
        if (ObjectUtils.isNotEmpty(recentNewsMo.getVideoTime())) {
            recentNewsMo.setVideoTime(recentNewsMo.getVideoTime());
        }
        if (StrUtil.isNotEmpty(recentNewsMo.getVideoPicture())) {
            recentNewsMo.setVideoPicture(recentNewsMo.getVideoPicture().trim());
        }
        if (ObjectUtils.isNotEmpty(recentNewsMo.getStatusType())) {
            recentNewsMo.setStatusType(recentNewsMo.getStatusType());
        }
        if (ObjectUtils.isNotEmpty(recentNewsMo.getSort())) {
            recentNewsMo.setSort(recentNewsMo.getSort());
        }
        recentNewsMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.recentNewsService.updateById(recentNewsMo));
    }

    /**
     * 最新动态删除
     */
    @ApiOperation(value = "根据主键删除最新动态单条数据", notes = "根据id删除")
    @PostMapping("/deleteById")
    public ResultMessageVo removeById(Integer id) {
        return ResultUtil.data(this.recentNewsService.removeById(id));
    }

    /**
     * 最新动态分页列表
     */
    @PostMapping("/page")
    @ApiOperation(value = "最新动态分页列表", notes = "最新动态分页列表")
    public ResultMessageVo<IPage<RecentNewsQo>> pageList(@RequestBody RecentNewsQo qo) {
        Page<RecentNewsQo> page = new Page<>(qo.getPage(), qo.getLimit());
        IPage<RecentNewsQo> recentNewsQoIPage = recentNewsService.pageList(page, qo);
        if (ObjectUtils.isEmpty(recentNewsQoIPage)) {
            return null;
        }
        return ResultUtil.data(recentNewsQoIPage);
    }

}
