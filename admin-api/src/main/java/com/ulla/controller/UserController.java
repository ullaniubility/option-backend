package com.ulla.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.BalanceMo;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.auth.mapper.LoginUserMapper;
import com.ulla.modules.auth.mapper.UserWalletConnectMapper;
import com.ulla.modules.auth.mo.LoginUserMo;
import com.ulla.modules.auth.mo.UserAuthorityMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.mo.UserWalletConnectMo;
import com.ulla.modules.auth.qo.UserAdminQo;
import com.ulla.modules.auth.qo.UserLevelQo;
import com.ulla.modules.auth.qo.UserWalletConnectQo;
import com.ulla.modules.auth.service.UserAuthorityService;
import com.ulla.modules.auth.service.UserLevelService;
import com.ulla.modules.auth.service.UserService;
import com.ulla.modules.auth.service.UserWalletConnectService;
import com.ulla.modules.auth.vo.*;
import com.ulla.modules.business.mo.MoneyDepositMo;
import com.ulla.modules.business.service.MoneyDepositService;
import com.ulla.modules.business.vo.HomeUserVo;
import com.ulla.modules.business.vo.MemberLoginVo;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.service.MoneyPaymentTransactionService;
import com.ulla.mybatis.util.PageUtil;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description {用户controller}
 * @author {clj}
 * @since {2023-2-8}
 */
@Api(value = "用户注册/登录/忘记密码/重置密码", tags = {"用户注册/登录/忘记密码/重置密码"})
@RestController
@RequestMapping("/sysUser")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    final UserService userService;

    final UserLevelService userLevelService;

    final UserWalletConnectMapper walletConnectMapper;

    final MoneyPaymentTransactionService moneyPaymentTransactionService;

    final BalanceService balanceService;

    final UserAuthorityService authorityService;

    final UserWalletConnectService userWalletConnectService;

    final MoneyDepositService moneyDepositService;

    final LoginUserMapper loginUserMapper;

    final Cache cache;

    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public ResultMessageVo login(@RequestBody UserVo userVo) {
        return userService.loginAdmin(userVo.getMail(), userVo.getPassword());
    }

    @ApiOperation("重置密码")
    @PostMapping(value = "/resetPassword")
    public ResultMessageVo resetPassword(@RequestBody UserVo userVo) {
        return userService.resetPassword(userVo.getOpenId(), userVo.getPassword());

    }

    @ApiOperation("获取用户详情资料")
    @GetMapping(value = "/getUserDetail")
    public ResultMessageVo getUserDetail(Long uid) {
        UserMo mo = userService.getById(uid);
        UserDetailVo userDetailVo = new UserDetailVo();
        BeanUtils.copyProperties(mo, userDetailVo);
        UserWalletConnectMo walletConnectMo = walletConnectMapper
            .selectOne(Wrappers.<UserWalletConnectMo>query().lambda().eq(UserWalletConnectMo::getUid, mo.getUid()));
        if (ObjectUtil.isEmpty(walletConnectMo) || ObjectUtil.isEmpty(walletConnectMo.getAddress())) {
            userDetailVo.setWalletAddress("");
        } else {
            userDetailVo.setWalletAddress(walletConnectMo.getAddress());
        }
        return ResultUtil.data(userDetailVo);
    }

    @ApiOperation("获取用户详情资产管理")
    @GetMapping(value = "/getUserCapital")
    public ResultMessageVo getUserCapital(Long uid) {
        BalanceMo trueBalance = balanceService
            .getOne(new LambdaQueryWrapper<BalanceMo>().eq(BalanceMo::getType, 1).eq(BalanceMo::getDeleteFlag, 0)
                .eq(BalanceMo::getUserId, uid).orderByDesc(BalanceMo::getCreateTime).last("limit 1"));
        UserCapitalVo userCapitalVo = new UserCapitalVo();
        if (ObjectUtil.isEmpty(trueBalance) || ObjectUtil.isEmpty(trueBalance.getBalance())) {
            userCapitalVo.setTrueBalance(BigDecimal.valueOf(0));
        } else {
            userCapitalVo.setTrueBalance(trueBalance.getBalance());
        }
        BalanceMo bonusBalance = balanceService
            .getOne(new LambdaQueryWrapper<BalanceMo>().eq(BalanceMo::getType, 0).eq(BalanceMo::getDeleteFlag, 0)
                .eq(BalanceMo::getUserId, uid).orderByDesc(BalanceMo::getCreateTime).last("limit 1"));
        if (ObjectUtil.isEmpty(bonusBalance) || ObjectUtil.isEmpty(bonusBalance.getBalance())) {
            userCapitalVo.setBonusBalance(BigDecimal.valueOf(0));
        } else {
            userCapitalVo.setBonusBalance(bonusBalance.getBalance());
        }
        BalanceMo one = balanceService
            .getOne(Wrappers.<BalanceMo>query().lambda().eq(BalanceMo::getUserId, uid).eq(BalanceMo::getType, 1));
        if (ObjectUtil.isEmpty(one) || ObjectUtil.isEmpty(one.getEo())) {
            userCapitalVo.setEo(0);
        } else {
            userCapitalVo.setEo(one.getEo());
        }
        BalanceMo simulatedBalance = balanceService
            .getOne(new LambdaQueryWrapper<BalanceMo>().eq(BalanceMo::getType, 3).eq(BalanceMo::getDeleteFlag, 0)
                .eq(BalanceMo::getUserId, uid).orderByDesc(BalanceMo::getCreateTime).last("limit 1"));
        if (ObjectUtil.isEmpty(simulatedBalance) || ObjectUtil.isEmpty(simulatedBalance.getBalance())) {
            userCapitalVo.setSimulatedBalance(BigDecimal.valueOf(0));
        } else {
            userCapitalVo.setSimulatedBalance(simulatedBalance.getBalance());
        }
        List<MoneyPaymentTransactionEntity> list = moneyPaymentTransactionService
            .list(new LambdaQueryWrapper<MoneyPaymentTransactionEntity>().eq(MoneyPaymentTransactionEntity::getUid, uid)
                .in(MoneyPaymentTransactionEntity::getOrderStatus, 1, 2));
        BigDecimal totalInvestment = list.stream().map(MoneyPaymentTransactionEntity::getEstimatedDepositAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCapitalVo.setTotalInvestment(totalInvestment);
        BigDecimal totalDisbursement = moneyDepositService
            .list(new LambdaQueryWrapper<MoneyDepositMo>().eq(MoneyDepositMo::getUid, uid)
                .in(MoneyDepositMo::getOrderStatus, 1, 2))
            .stream().map(MoneyDepositMo::getDepositMonetaryAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        userCapitalVo.setTotalDisbursement(totalDisbursement);
        return ResultUtil.data(userCapitalVo);
    }

    @ApiOperation("编辑用户")
    @PostMapping(value = "/editUser")
    public ResultMessageVo editUser(@RequestBody UserVo userVo) {
        return userService.editUser(userVo);
    }

    @ApiOperation("管理员编辑用户")
    @PostMapping(value = "/adminEditUser")
    public ResultMessageVo adminEditUser(@RequestBody AdminUserVo adminUserVo) {
        if (userService.adminEditUser(adminUserVo) == 1) {
            return ResultUtil.success();
        }
        return ResultUtil.error(ResultCodeEnums.USER_EDIT_ERROR);
    }

    /**
     * 对用户进行禁用启用
     */
    @GetMapping("/updateDeleteFlag")
    @ApiOperation(value = "对用户进行禁用启用", notes = "通过uid对用户进行禁用启用")
    public ResultMessageVo updateDeleteFlag(Long uid, Integer deleteFlag) {
        UpdateWrapper<UserMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).set("delete_flag", deleteFlag);
        if (userService.update(null, updateWrapper)) {
            if (deleteFlag.intValue() == 1) {
                LambdaQueryWrapper<LoginUserMo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(LoginUserMo::getUid, uid);
                List<LoginUserMo> list = loginUserMapper.selectList(lambdaQueryWrapper);
                if (list.size() > 0) {
                    list.stream().forEach(loginUserMo -> {
                        cache.remove(loginUserMo.getToken());
                    });
                    loginUserMapper.deleteBatchIds(list);
                }
                return ResultUtil.error(200, ResultCodeEnums.USER_STATUS_OFF.message());
            }
            return ResultUtil.success(200, ResultCodeEnums.USER_STATUS_ON.message());
        }
        return ResultUtil.error(ResultCodeEnums.USER_STATUS_ERROR);
    }

    @ApiOperation(value = "获取用户分页信息")
    @GetMapping("/getByPage")
    public ResultMessageVo<IPage<UserPageVo>> getByPage(UserAdminQo qo) {
        IPage<UserPageVo> userPageVoIPage = userService.userPageAdmin(qo);
        if (ObjectUtils.isEmpty(userPageVoIPage)) {
            return null;
        }
        return ResultUtil.data(userPageVoIPage);
    }

    /**
     * 获取首页的最近登录用户账户信息(展示前10条)
     */
    @ApiOperation(value = "获取首页的最近登录用户账户信息")
    @GetMapping("/getList")
    public ResultMessageVo<List<MemberLoginVo>> getList() {
        return ResultUtil.data(userService.getList());
    }

    /**
     * 首页用户数据
     */
    @ApiOperation(value = "获取首页的最近登录用户账户信息")
    @GetMapping("/getMember")
    public ResultMessageVo<HomeUserVo> getMember() {
        return ResultUtil.data(userService.getMember());
    }

    @ApiOperation("用户退出")
    @PostMapping(value = "/logout")
    public ResultMessageVo logout() {
        return userService.logout();
    }

    @RequestMapping(value = "/forward")
    public ResultMessageVo forward() {
        return ResultUtil.error(8657, "Please log in first");
    }

    /**
     * 对用户进行禁用启用
     */
    // @GetMapping("/updateDeleteFlag")
    // @ApiOperation(value = "对用户进行禁用启用", notes = "通过uid对用户进行禁用启用")
    // public ResultMessageVo updateDeleteFlag(Long uid, Integer deleteFlag) {
    // UserMo userMo = new UserMo();
    // BeanUtils.copyProperties(vo,userMo);
    // // UpdateWrapper<UserMo> updateWrapper = new UpdateWrapper<>();
    // // updateWrapper.set("deleteFlag", deleteFlag);
    // // updateWrapper.eq("uid", uid);
    // // return ResultUtil.data(userService.update(updateWrapper));
    // return ResultUtil.data(userService.updateById(userMo));
    // }

    /**
     * 会员等级列表
     */
    @ApiOperation("会员等级列表")
    @GetMapping(value = "/userLevelList")
    public ResultMessageVo userLevelList(PageVo pageVo) {
        if (StringUtils.isBlank(pageVo.getOrder())) {
            pageVo.setSort("sortNum");
            pageVo.setOrder("desc");
        }
        return ResultUtil.data(userLevelService.page(PageUtil.initPage(pageVo), null));
    }

    /**
     * 编辑/禁用会员等级
     */
    @ApiOperation("编辑/禁用会员等级")
    @PostMapping(value = "/updateUserLevel")
    public ResultMessageVo updateUserLevel(@RequestBody UserLevelQo userLevelQo) {
        return userLevelService.updateUserLevel(userLevelQo);
    }

    /**
     * 新增会员等级
     */
    @ApiOperation("新增会员等级")
    @PostMapping(value = "/addUserLevel")
    public ResultMessageVo addUserLevel(@RequestBody UserLevelQo userLevelQo) {
        return userLevelService.addUserLevel(userLevelQo);
    }

    /**
     * 查看用户是否拥有提现权限
     */
    @ApiOperation(value = "查看用户是否拥有提现权限", notes = "根据uid查看用户是否拥有提现权限")
    @GetMapping("/getAllow")
    public ResultMessageVo<UserAuthorityMo> getAllow(@NotNull(message = "数据编号不能为空") Long uid) {
        return ResultUtil.data(authorityService.getAllow(uid));
    }

    /**
     * 修改用户的提现权限
     */
    @GetMapping("/updateAlloweWithdrawal")
    @ApiOperation(value = "修改用户的提现权限", notes = "通过uid修改用户的提现权限")
    public ResultMessageVo updateAlloweWithdrawal(Long uid, Integer alloweWithdrawal) {
        UpdateWrapper<UserAuthorityMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).set("allowe_withdrawal", alloweWithdrawal);
        return ResultUtil.data(authorityService.update(updateWrapper));
    }

    /**
     * 会员提现地址分页列表
     */
    @ApiOperation("会员提现地址分页列表")
    @PostMapping(value = "/listByWithdrawal")
    public ResultMessageVo listByWithdrawal(@RequestBody UserWalletConnectQo userWalletConnectQo) {
        Page<UserWalletConnectQo> page = new Page<>(userWalletConnectQo.getPage(), userWalletConnectQo.getPageSize());
        IPage<UserWalletConnectMo> userWalletConnectMoIPage =
            userWalletConnectService.listByWithdrawal(page, userWalletConnectQo);
        if (ObjectUtils.isEmpty(userWalletConnectMoIPage)) {
            return null;
        }
        return ResultUtil.data(userWalletConnectMoIPage);
    }

}
