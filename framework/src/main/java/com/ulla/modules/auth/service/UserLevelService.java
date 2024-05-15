package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.auth.qo.UserLevelQo;

/**
 * @Description 用户等级
 * @author zhuyongdong
 * @since 2023-05-16 20:17:50
 */
public interface UserLevelService extends IService<UserLevelMo> {

    ResultMessageVo getList();

    ResultMessageVo updateUserLevel(UserLevelQo userLevelQo);

    ResultMessageVo addUserLevel(UserLevelQo userLevelQo);
}
