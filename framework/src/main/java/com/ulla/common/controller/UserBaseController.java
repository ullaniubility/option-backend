package com.ulla.common.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

import static com.ulla.constant.NumberConstant.ZERO;

/**
 * <p>
 * 促销优惠券表 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Component
public class UserBaseController {

    @Resource
    UserService userService;

    public Long getUserId(String uid) {
        Assert.notBlank(uid,"参数错误");
        UserMo user = userService.getOne(new LambdaQueryWrapper<UserMo>().eq(UserMo::getOpenId, uid).eq(UserMo::getDeleteFlag, ZERO).last("limit 1"));
        Assert.notNull(user,"用户不存在");
        return user.getUid();
    }

}
