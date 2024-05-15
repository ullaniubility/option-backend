package com.ulla.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.service.ThirdLoginService;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2022-06-01
 */
@RestController
@RequestMapping("/authorize")
@AllArgsConstructor
public class AuthController {

    final ThirdLoginService thirdLoginService;

    @GetMapping("/getAuthorize")
    public ResultMessageVo getAuthorize(@RequestParam("source") String source) {
        return ResultUtil.data(thirdLoginService.getAuthorize(source));
    }
}
