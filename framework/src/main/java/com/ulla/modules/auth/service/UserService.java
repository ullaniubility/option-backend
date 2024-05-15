package com.ulla.modules.auth.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.VerificationConstant;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.qo.UserAdminQo;
import com.ulla.modules.auth.vo.AdminUserVo;
import com.ulla.modules.auth.vo.UserPageVo;
import com.ulla.modules.auth.vo.UserVo;
import com.ulla.modules.business.vo.HomeUserVo;
import com.ulla.modules.business.vo.MemberLoginVo;

/**
 * @author {clj}
 * @Description {用户service}
 * @since {2023-2-8}
 */
public interface UserService extends IService<UserMo> {

    ResultMessageVo forgetPassword();

    ResultMessageVo resetPassword(String openId, String password);

    ResultMessageVo register(String mail, String password);

    ResultMessageVo login(String mail, String password);

    ResultMessageVo checkCode(String openId, String code, VerificationConstant.Verify_Type type);

    ResultMessageVo fingerprint();

    // ResultMessageVo logOut(String openId);

    ResultMessageVo getUserDetail();

    ResultMessageVo editUser(UserVo userVo);

    ResultMessageVo setPortrait(MultipartFile file, String openId);

    ResultMessageVo changePassword(Long uid, String mail, String password, String newPassword);

    ResultMessageVo getSysConfig(String configKey);

    ResultMessageVo loginThird(JSONObject jsonObject);

    ResultMessageVo loginAdmin(String mail, String password);

    IPage<UserPageVo> userPageAdmin(UserAdminQo userAdminQo);

    ResultMessageVo logout();

    List<MemberLoginVo> getList();

    HomeUserVo getMember();

    int adminEditUser(AdminUserVo adminUserVo);

    ResultMessageVo getPopConfig(String configKey);
}
