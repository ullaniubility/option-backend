package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.QuestionMo;
import com.ulla.modules.business.service.IQuestionService;

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
     * 客户端无分页
     */
    @ApiOperation(value = "常见问题列表", notes = "常见问题列表")
    @GetMapping("/list")
    public ResultMessageVo QuestionList() {
        LambdaQueryWrapper<QuestionMo> wrapper = new LambdaQueryWrapper<>();
        return ResultUtil.data(
            questionService.list(wrapper.orderByDesc(QuestionMo::getUpdateTime).eq(QuestionMo::getStatusType, 1)));
    }
}
