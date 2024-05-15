package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.QuestionMo;
import com.ulla.modules.business.qo.QuestionQo;
import com.ulla.modules.business.service.IQuestionService;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "常见问题")
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionController {

    final IQuestionService questionService;

    /**
     * 常见问题新增
     */
    @PostMapping("/save")
    @ApiOperation(value = "新增常见问题", notes = "新增常见问题")
    public ResultMessageVo save(@RequestBody QuestionMo questionMo) {
        if (questionMo == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (StrUtil.isEmpty(questionMo.getQuestion()) || StrUtil.isEmpty(questionMo.getAnswer())
            || ObjectUtils.isEmpty(questionMo.getStatusType()) || ObjectUtils.isEmpty(questionMo.getSort())) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        if (this.questionService.getOne(
            Wrappers.<QuestionMo>lambdaQuery().eq(QuestionMo::getQuestion, questionMo.getQuestion()), false) != null) {
            return ResultUtil.error(4002, "Title repetition");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        questionMo.setQuestion(questionMo.getQuestion().trim());
        questionMo.setAnswer(questionMo.getAnswer().trim());
        questionMo.setStatusType(questionMo.getStatusType());
        questionMo.setSort(questionMo.getSort());
        questionMo.setCreateTime(System.currentTimeMillis());
        questionMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.questionService.save(questionMo));
    }

    /**
     * 常见问题编辑
     */
    @PostMapping("/update")
    @ApiOperation(value = "编辑常见问题", notes = "编辑常见问题")
    public ResultMessageVo update(@RequestBody QuestionMo questionMo) {
        if (questionMo == null || questionMo.getId() == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        if (StrUtil.isNotEmpty(questionMo.getQuestion())) {
            questionMo.setQuestion(questionMo.getQuestion().trim());
        }
        if (StrUtil.isNotEmpty(questionMo.getAnswer())) {
            questionMo.setAnswer(questionMo.getAnswer().trim());
        }
        if (ObjectUtils.isNotEmpty(questionMo.getStatusType())) {
            questionMo.setStatusType(questionMo.getStatusType());
        }
        if (ObjectUtils.isNotEmpty(questionMo.getSort())) {
            questionMo.setSort(questionMo.getSort());
        }
        questionMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.questionService.updateById(questionMo));
    }

    /**
     * 常见问题删除
     */
    @ApiOperation(value = "根据主键删除常见问题数据", notes = "根据id删除")
    @PostMapping("/deleteById")
    public ResultMessageVo removeById(@RequestBody QuestionQo questionQo) {
        return ResultUtil.data(this.questionService.removeById(questionQo.getId()));
    }

    /**
     * 
     * 常见问题分页列表
     */
    @PostMapping("/page")
    @ApiOperation(value = "常见问题分页列表", notes = "常见问题分页列表")
    public ResultMessageVo<IPage<QuestionQo>> pageList(@RequestBody QuestionQo qo) {
        Page<QuestionQo> page = new Page<>(qo.getPage(), qo.getLimit());
        IPage<QuestionQo> questionQoIPage = questionService.pageList(page, qo);
        if (ObjectUtils.isEmpty(questionQoIPage)) {
            return null;
        }
        return ResultUtil.data(questionQoIPage);
    }
}
