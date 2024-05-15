package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.service.ActiveService;
import com.ulla.modules.assets.vo.ActiveVo;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 促销活动表 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@RestController
@RequestMapping("/active")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ActiveController {

    final ActiveService activeService;

    @PostMapping(value = "/save")
    public ResultMessageVo saveActiveAndSyncCreateCoupon(@RequestBody ActiveVo activeVo) {
        return activeService.saveActiveAndSyncCreateCoupon(activeVo);
    }
}
