package com.ulla.modules.auth.service.impl;

import static com.ulla.constant.NumberConstant.ZERO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.cache.CachePrefix;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.*;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.*;
import com.ulla.modules.admin.mapper.SysConfigMapper;
import com.ulla.modules.assets.mapper.ActiveCouponMapper;
import com.ulla.modules.assets.mapper.ActiveMapper;
import com.ulla.modules.assets.mo.ActiveCouponMo;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.auth.mapper.*;
import com.ulla.modules.auth.mo.*;
import com.ulla.modules.auth.qo.UserAdminQo;
import com.ulla.modules.auth.qo.UserQo;
import com.ulla.modules.auth.service.ThirdLoginService;
import com.ulla.modules.auth.service.UserService;
import com.ulla.modules.auth.vo.AdminUserVo;
import com.ulla.modules.auth.vo.UserPageVo;
import com.ulla.modules.auth.vo.UserVo;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mo.LoginDeviceMo;
import com.ulla.modules.business.vo.HomeUserVo;
import com.ulla.modules.business.vo.MemberLoginVo;
import com.ulla.modules.business.vo.MoneyPopoverVo;
import com.ulla.mybatis.util.PageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description {用户serviceImpl}
 * @author {clj}
 * @since {2023-2-8}
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl extends ServiceImpl<UserMapper, UserMo> implements UserService {

    final static String ETH = "eip155";

    final Cache cache;

    final UserMapper userMapper;

    final UserLoginInfoMapper userLoginInfoMapper;

    final SysConfigMapper sysConfigMapper;

    final ThirdLoginService thirdLoginService;

    final LoginUserMapper loginUserMapper;

    final OrderMapper orderMapper;

    final UserLoginDeviceMapper deviceMapper;

    final UserWalletConnectMapper walletConnectMapper;

    final ActiveCouponMapper couponMapper;

    final ActiveMapper activeMapper;

    @Lazy
    @Autowired
    private BalanceService balanceService;

    public ResultMessageVo forgetPassword() {
        // 发送验证码验证
        return null;
    }

    /**
     * * 重置密码
     *
     * @param openId
     *            openId
     * @param password
     *            password
     * @return ResultMessageVo * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo resetPassword(String openId, String password) {
        try {
            UserMo userMo = userMapper.selectByOpenId(openId);
            userMo.setPassword(TokenUtils.encoded(password));
            userMapper.updateById(userMo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * * 修改密码
     *
     * @param uid
     *            uid
     * @param password
     *            password
     * @return ResultMessageVo * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo changePassword(Long uid, String mail, String password, String newPassword) {
        try {
            UserMo userMo = userMapper.selectById(uid);
            if (userMo == null) {
                return ResultUtil.error(ResultCodeEnums.USER_NOT_EXIST);
            }
            if (!(userMo.getMail().equals(mail) && TokenUtils.decoded(userMo.getPassword()).equals(password))) {
                return ResultUtil.error(ResultCodeEnums.USER_OLD_PASSWORD_ERROR);
            }
            userMo.setPassword(TokenUtils.encoded(newPassword));
            userMapper.updateById(userMo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * * 获取系统配置
     *
     * @param configKey
     *            configKey
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo getSysConfig(String configKey) {
        return ResultUtil.data(sysConfigMapper.selectByKey(configKey));
    }

    /**
     * 注册* *
     *
     * @param mail
     *            mail
     * @param password
     *            password request
     * @return ResultMessageVo ResultMessageVo
     */
    @Override
    public ResultMessageVo register(String mail, String password) {
        Map<String, String> map = new HashMap<>();
        try {
            String fingerprint = UserUtil.getFingerprint();
            if (fingerprint == null) {
                return ResultUtil.error(ResultCodeEnums.FINGERPRINT_NOT_EXIST);
            }
            List<UserMo> userMos = userMapper.selectByMail(mail);
            if (!userMos.isEmpty()) {
                return ResultUtil.error(ResultCodeEnums.USER_CONNECT_BANDING_ERROR);
            }

            UserMo user = userMapper.selectDemoAccount(fingerprint);
            user.setFingerprint("ok");
            user.setMail(mail);
            user.setPassword(TokenUtils.encoded(password));
            // 生成邀请码
            user.setInvitationCode(genRandomNum());
            // 注册默认为正式用户
            user.setType(1);
            userMapper.updateById(user);
            String token = UserUtil.getToken();
            map.put("openId", user.getOpenId());
            map.put("mail", user.getMail());
            map.put("type", user.getType().toString());
            map.put("token", token);
            ResultUtil.data(map);
            cache.put(token, user, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
            return ResultUtil.data(map);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.data(map);
    }

    /**
     * * 登录
     *
     * @param mail
     *            mail
     * @param password
     *            password
     * @return
     */
    @Override
    public ResultMessageVo login(String mail, String password) {
        Map<String, String> map = new HashMap<>();
        try {
            UserMo userMo = userMapper.selectByLogin(mail, TokenUtils.encoded(password));
            if (userMo == null) {
                return ResultUtil.error(ResultCodeEnums.USER_PASSWORD_ERROR);
            } else {
                log.info("============查询用户信息userMo:{}", userMo);
                if (UserUtil.getToken() != null) {
                    if (cache.hasKey(UserUtil.getToken())) {
                        log.info("============移除原有缓存token:{}", UserUtil.getToken());
                        cache.remove(UserUtil.getToken());
                    }
                }
                String token = getToken(userMo);
                map.put("portraitAddress", userMo.getPortraitAddress());
                map.put("openId", userMo.getOpenId());
                map.put("mail", userMo.getMail());
                map.put("type", userMo.getType().toString());
                map.put("token", token);
                log.info("============缓存token:{}", token);
                cache.put(token, userMo, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
                try {
                    log.info("============生成登录日志");
                    // 生成登录日志
                    createLoginInfo(userMo);
                    LoginDeviceMo infoMo = new LoginDeviceMo();
                    infoMo.setUid(userMo.getUid());
                    String ip = IpUtils.getIpAddressWithoutCheck();
                    infoMo.setIp(ip);
                    infoMo.setArea(IpUtils.getCityInfo(ip));
                    infoMo.setToken(token);
                    infoMo.setCreateTime(System.currentTimeMillis());
                    LoginDeviceMo loginDeviceMo = deviceMapper.selectByIp(token, infoMo.getUid());
                    if (ObjectUtils.isEmpty(loginDeviceMo)) {
                        deviceMapper.insert(infoMo);
                    } else {
                        infoMo.setId(loginDeviceMo.getId());
                        deviceMapper.updateById(infoMo);
                    }
                } catch (Exception e) {
                    log.info("============生成登录日志异常");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("============map:{}", map);
        return ResultUtil.data(map);
    }

    @Override
    public ResultMessageVo logout() {
        if (cache.hasKey(UserUtil.getToken())) {
            cache.remove(UserUtil.getToken());
        }
        loginUserMapper.deleteByOpenId(UserUtil.getToken());
        return ResultUtil.success();
    }

    /**
     * * 第三方登录
     *
     * @param jsonObject
     *            jsonObject
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo loginThird(JSONObject jsonObject) {
        try {
            log.debug("-----------------三方登录返回信息------------------------");
            log.debug(jsonObject.toString());
            String address = jsonObject.getString("address").toLowerCase();
            String netId = jsonObject.getString("netId");
            String net = jsonObject.getString("net");
            if (StringUtils.isBlank(address)) {
                return ResultUtil.error(ResultCodeEnums.USER_CONNECT_ERROR);
            }
            LambdaQueryWrapper<UserWalletConnectMo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserWalletConnectMo::getAddress, address);
            queryWrapper.eq(UserWalletConnectMo::getNet, net);
            queryWrapper.eq(UserWalletConnectMo::getNetId, netId);
            UserWalletConnectMo walletConnectMo = walletConnectMapper.selectOne(queryWrapper);
            String ip = IpUtils.getIpAddressWithoutCheck();
            String area = IpUtils.getCityInfo(ip);
            Map<String, String> map = new HashMap<>();
            String token = null;
            UserLoginInfoMo infoMo = new UserLoginInfoMo();

            if (null == walletConnectMo || (netId.contains(ETH) && !walletConnectMo.getNetId().equals(netId))) {
                Long uid = UserUtil.getUid();
                String openId = UserUtil.getOpenId();
                walletConnectMo = new UserWalletConnectMo();
                walletConnectMo.setUid(uid);
                walletConnectMo.setOpenId(openId);
                walletConnectMo.setNet(net);
                walletConnectMo.setNetId(netId);
                walletConnectMo.setAddress(address);
                walletConnectMapper.insert(walletConnectMo);
                // 当前用户
                UserMo userMo = userMapper.selectById(uid);
                if (null == uid || null == userMo) {
                    return ResultUtil.error(ResultCodeEnums.ILLEGAL_REQUEST_ERROR);
                }
                // 非正式用户,直接绑定,做正式用户变更
                if (userMo.getType().intValue() == NumberConstant.ZERO) {
                    userMo.setArea(area);
                    userMo.setType(1);
                    userMo.setFingerprint("ok");
                    userMapper.updateById(userMo);
                    token = getToken(userMo);
                    map.put("portraitAddress", userMo.getPortraitAddress());
                    map.put("openId", userMo.getOpenId());
                    map.put("mail", null);
                    map.put("type", userMo.getType().toString());
                    map.put("token", token);
                    infoMo.setUid(userMo.getUid());
                    cache.put(token, userMo, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
                } else {
                    try {
                        // 未传递uid, 也没有历史demo账号, 生成新的demo账号
                        UserMo user = new UserMo();
                        // 用前缀加时间戳作为主键
                        user.setUid(Long.valueOf(UserConstant.USER_PREFIX + System.currentTimeMillis()));
                        // uid使用三组四位随机数
                        user.setOpenId(randomNumber());
                        user.setFingerprint("ok");
                        // 初次打开调默认为demo账号
                        user.setType(NumberConstant.ONE);
                        user.setPermissions(UserConstant.MEMBER);
                        user.setArea(area);
                        token = getToken(user);
                        userMapper.insert(user);
                        balanceService.getUserBalance(user.getUid());
                        map.put("portraitAddress", user.getPortraitAddress());
                        map.put("openId", user.getOpenId());
                        map.put("mail", null);
                        map.put("type", user.getType().toString());
                        map.put("token", token);
                        infoMo.setUid(user.getUid());
                        cache.put(token, user, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            } else {
                UserMo userMo = userMapper.selectById(walletConnectMo.getUid());
                if (null == userMo) {
                    return ResultUtil.error(ResultCodeEnums.USER_CONNECT_LOGIN_ERROR);
                }
                if (BooleanUtils.toBoolean(userMo.getDeleteFlag())) {
                    return ResultUtil.error(ResultCodeEnums.USER_STATUS_OFF);
                }
                token = getToken(userMo);
                map.put("portraitAddress", userMo.getPortraitAddress());
                map.put("openId", userMo.getOpenId());
                map.put("mail", null);
                map.put("type", userMo.getType().toString());
                map.put("token", token);
                infoMo.setUid(userMo.getUid());
                cache.put(token, userMo, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
            }
            // 记录登录日志
            try {
                infoMo.setFingerprint("ok");
                infoMo.setIp(ip);
                infoMo.setArea(area);
                infoMo.setIfGuest(NumberConstant.ZERO);
                userLoginInfoMapper.insert(infoMo);
                LoginDeviceMo mo = new LoginDeviceMo();
                mo.setUid(infoMo.getUid());
                String ipMo = IpUtils.getIpAddressWithoutCheck();
                mo.setIp(ipMo);
                mo.setArea(IpUtils.getCityInfo(ipMo));
                mo.setToken(token);
                mo.setCreateTime(System.currentTimeMillis());
                LoginDeviceMo loginDeviceMo = deviceMapper.selectByIp(token, mo.getUid());
                if (ObjectUtils.isEmpty(loginDeviceMo)) {
                    deviceMapper.insert(mo);
                } else {
                    mo.setId(loginDeviceMo.getId());
                    deviceMapper.updateById(mo);
                }
            } catch (Exception e) {
                log.info("============生成登录日志异常");
            }
            return ResultUtil.data(map);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.error(ResultCodeEnums.USER_CONNECT_ERROR);
    }

    @Override
    public ResultMessageVo loginAdmin(String mail, String password) {
        Map<String, String> map = new HashMap<>();
        try {
            UserMo userMo = userMapper.selectByLogin(mail, TokenUtils.encoded(password));
            if (userMo == null) {
                return ResultUtil.error(ResultCodeEnums.USER_PASSWORD_ERROR);
            } else {
                log.info("============查询用户信息userMo:{}", userMo);
                if (UserUtil.getToken() != null) {
                    if (cache.hasKey(UserUtil.getToken())) {
                        log.info("============移除原有缓存token:{}", UserUtil.getToken());
                        cache.remove(UserUtil.getToken());
                    }
                }
                String token = getToken(userMo);
                map.put("portraitAddress", userMo.getPortraitAddress());
                map.put("openId", userMo.getOpenId());
                map.put("mail", userMo.getMail());
                map.put("type", userMo.getType().toString());
                map.put("token", token);
                map.put("permission", userMo.getPermissions());
                log.info("============缓存token:{}", token);
                cache.put(token, userMo, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
                try {
                    log.info("============生成登录日志");
                    // 生成登录日志
                    createLoginInfo(userMo);
                    LoginDeviceMo infoMo = new LoginDeviceMo();
                    infoMo.setUid(userMo.getUid());
                    String ip = IpUtils.getIpAddressWithoutCheck();
                    infoMo.setIp(ip);
                    infoMo.setArea(IpUtils.getCityInfo(ip));
                    infoMo.setToken(token);
                    infoMo.setCreateTime(System.currentTimeMillis());
                    LoginDeviceMo loginDeviceMo = deviceMapper.selectByIp(token, infoMo.getUid());
                    if (ObjectUtils.isEmpty(loginDeviceMo)) {
                        deviceMapper.insert(infoMo);
                    } else {
                        infoMo.setId(loginDeviceMo.getId());
                        deviceMapper.updateById(infoMo);
                    }
                } catch (Exception e) {
                    log.info("============生成登录日志异常");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return ResultUtil.data(map);
    }

    @Override
    public IPage<UserPageVo> userPageAdmin(UserAdminQo userAdminQo) {
        return userMapper.userPageAdmin(PageUtil.initPage(userAdminQo), userAdminQo.queryWrapper());
    }

    @Override
    public List<MemberLoginVo> getList() {
        List<MemberLoginVo> list = baseMapper.getList();
        return list;
    }

    @Override
    public HomeUserVo getMember() {
        // 当前在线人数
        int onlineNum = 0;
        if (cache.hasKey(RedisConstant.User.USER_ONLINE_NUM)) {
            onlineNum = (int)cache.get(RedisConstant.User.USER_ONLINE_NUM);
        }

        // 今日新注册人数
        long timeMillis = System.currentTimeMillis();
        long todayEnterBeginTime =
            timeMillis - (timeMillis + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
        Long todayEnterEndTime = todayEnterBeginTime + 86400000;
        int addMember = baseMapper.addMember(todayEnterBeginTime, todayEnterEndTime);
        // 会员总数
        int totalMember = baseMapper.getTotalMember();
        HomeUserVo homeUserVo = new HomeUserVo();
        homeUserVo.setTotalMember(totalMember);
        homeUserVo.setAddMember(addMember);
        // 当前开放订单数
        HomeUserVo openOrder = orderMapper.getOpenOrder();
        homeUserVo.setOpenOrder(openOrder.getOpenOrder());
        // 当前开放订单额
        homeUserVo.setOpenAmount(openOrder.getOpenAmount());
        homeUserVo.setOnlineMember(onlineNum);
        return homeUserVo;
    }

    @Override
    public int adminEditUser(AdminUserVo adminUserVo) {
        UserMo userMo = new UserMo();
        BeanUtils.copyProperties(adminUserVo, userMo);
        return userMapper.updateById(userMo);
    }

    @Override
    public ResultMessageVo getPopConfig(String configKey) {
        Map<String, Object> map = new HashMap<>();
        Long uid = UserUtil.getUid();
        List<LoginDeviceMo> deviceMos = deviceMapper.selectByUidAsc(uid);
        long time = 0L;
        if (!deviceMos.isEmpty()) {
            time = System.currentTimeMillis() - deviceMos.get(0).getCreateTime();
        }
        String s = sysConfigMapper.selectByKey(configKey);
        MoneyPopoverVo moneyPopoverVo = JSONArray.parseObject(s, MoneyPopoverVo.class);
        String activeId = moneyPopoverVo.getActiveId();
        map.put("value", JSONArray.parse(s));
        map.put("time", time);
        if (StringUtils.isEmpty(activeId)) {
            return ResultUtil.data(map);
        }
        ActiveMo activeMo = activeMapper.selectById(activeId);
        if (activeMo == null) {
            return ResultUtil.data(map);
        } else {
            QueryWrapper<ActiveCouponMo> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", uid);
            wrapper.eq("active_id", activeId);
            wrapper.eq("use_flag", NumberConstant.ONE);
            List<ActiveCouponMo> list = couponMapper.selectList(wrapper);
            if (!list.isEmpty()) {
                return ResultUtil.data(map);
            }
            QueryWrapper<ActiveCouponMo> couponWrapper = new QueryWrapper<>();
            couponWrapper.eq("use_flag", ZERO);
            couponWrapper.eq("active_id", activeId);
            List<ActiveCouponMo> coupons = couponMapper.selectList(couponWrapper);
            ActiveCouponMo build;
            if (!coupons.isEmpty()) {
                build = coupons.get(ZERO);
            } else {
                build = ActiveCouponMo.builder().activeId(Long.valueOf(activeId))
                    .couponCode(BusinessNoUtil.generateShortUuid()).useFlag(ZERO).build();
                couponMapper.insert(build);
            }
            map.put("coupon", build);
        }
        return ResultUtil.data(map);
    }

    private void createLoginInfo(UserMo userMo) throws Exception {
        UserLoginInfoMo infoMo = new UserLoginInfoMo();
        infoMo.setUid(userMo.getUid());
        infoMo.setFingerprint(UserUtil.getFingerprint());
        String ip = IpUtils.getIpAddressWithoutCheck();
        infoMo.setIp(ip);
        infoMo.setArea(IpUtils.getCityInfo(ip));
        infoMo.setIfGuest(NumberConstant.ZERO);
        userLoginInfoMapper.insert(infoMo);
    }

    /**
     * *校验验证码
     *
     * @param uid
     *            uid
     * @param code
     *            code
     * @param type
     *            type
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo checkCode(String uid, String code, VerificationConstant.Verify_Type type) {
        try {
            // 验证码为空
            if (StringUtils.isBlank(code)) {
                return ResultUtil.error(ResultCodeEnums.VERIFICATION_SMS_CHECKED_ERROR);
            }
            // 验证码已过期
            String redisCode = (String)cache.get(CachePrefix.VERIFICATION + uid + "-" + type);
            if (StringUtils.isBlank(redisCode)) {
                return ResultUtil.error(ResultCodeEnums.VERIFICATION_CODE_INVALID);
            }
            // 验证码错误
            if (!code.equals(redisCode)) {
                return ResultUtil.error(ResultCodeEnums.VERIFICATION_ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 每次打开软件传递fingerprint进来 *
     *
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo fingerprint() {
        Map<String, String> map = new HashMap<>();
        try {
            String fingerprint = UserUtil.getFingerprint();
            if (StringUtils.isBlank(fingerprint)) {
                return ResultUtil.error(ResultCodeEnums.FINGERPRINT_NOT_EXIST);
            }
            UserMo userMoDemo = userMapper.selectDemoAccount(fingerprint);
            String token;
            if (userMoDemo == null) {
                String ip = IpUtils.getIpAddressWithoutCheck();
                String area = IpUtils.getCityInfo(ip);
                // 未传递uid, 也没有历史demo账号, 生成新的demo账号
                UserMo user = new UserMo();
                // 用前缀加时间戳作为主键
                user.setUid(Long.valueOf(UserConstant.USER_PREFIX + System.currentTimeMillis()));
                // uid使用三组四位随机数
                user.setOpenId(randomNumber());
                user.setFingerprint(fingerprint);
                // 初次打开调默认为demo账号
                user.setType(NumberConstant.ZERO);
                user.setPermissions(UserConstant.MEMBER);
                // 初始化用户默认等级
                Gson gson = new Gson();
                Type type = new TypeToken<List<UserLevelMo>>() {}.getType();
                List<UserLevelMo> list = gson.fromJson(cache.get("user:level:list").toString(), type);
                if (list.size() > 0) {
                    user.setUserLevel(list.get(0).getLevel());
                }
                user.setArea(area);
                token = getToken(user);
                userMapper.insert(user);
                balanceService.getUserBalance(user.getUid());
                map.put("openId", user.getOpenId());
                map.put("mail", null);
                map.put("type", user.getType().toString());
                map.put("token", token);
                // 记录游客的登录日志
                UserLoginInfoMo infoMo = new UserLoginInfoMo();
                infoMo.setUid(user.getUid());
                infoMo.setFingerprint(user.getFingerprint());

                infoMo.setIp(ip);
                infoMo.setArea(area);
                infoMo.setIfGuest(1);
                userLoginInfoMapper.insert(infoMo);
                cache.put(token, user, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
            } else {
                token = getToken(userMoDemo);
                // 存在demo账号
                map.put("openId", userMoDemo.getOpenId());
                map.put("mail", null);
                map.put("type", userMoDemo.getType().toString());
                map.put("token", token);
                cache.put(token, userMoDemo, TokenUtils.JWT_EXPIRE_TIME_LONG, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.data(map);
    }

    /**
     * 生成token
     *
     * @param user
     *            user
     * @return String
     */
    private String getToken(UserMo user) {
        long millis = System.currentTimeMillis();
        // 已存在
        LoginUserMo loginUser = loginUserMapper.selectByOpenId(user.getOpenId());
        if (loginUser != null) {
            loginUser.setLoginTime(millis);
            // 刷新token前将实体token设置为空减少token长度
            loginUser.setToken(null);
            String token = TokenUtils.createToken(loginUser);
            loginUser.setToken(token);
            loginUserMapper.updateById(loginUser);
            return token;
        } else {
            loginUser = new LoginUserMo();
            loginUser.setLoginTime(millis);
            loginUser.setPermissions(user.getPermissions());
            loginUser.setUid(user.getUid());
            loginUser.setOpenId(user.getOpenId());
            loginUser.setUserLevel(user.getUserLevel());
            String token = TokenUtils.createToken(loginUser);
            loginUser.setToken(token);
            loginUserMapper.insert(loginUser);
            return token;
        }
    }

    /**
     * 获取用户详情* *
     *
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo getUserDetail() {
        if (UserUtil.getOpenId() == null) {
            ResultUtil.error(ResultCodeEnums.USER_AUTH_EXPIRED);
        }
        UserQo userQo = null;
        try {
            userQo = userMapper.selectByOpenIdForShow(UserUtil.getOpenId());
            if (ObjectUtils.isEmpty(userQo.getPhone())) {
                userQo.setPhone("");
            }
        } catch (Exception e) {
            log.info("获取到的用户信息失败=====================");
            log.error(e.getMessage());
        }
        return ResultUtil.data(userQo);
    }

    /**
     * 编辑用户* *
     *
     * @param userVo
     *            userVo
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo editUser(UserVo userVo) {
        try {
            UserMo userMo = new UserMo();
            userMo.setUid(UserUtil.getUid());
            userMo.setPostalCode(userVo.getPostalCode());
            userMo.setPortraitAddress(userVo.getPortraitAddress());
            userMo.setPhone(userVo.getPhone());
            userMo.setAddress(userVo.getAddress());
            userMo.setUserAddress(userVo.getUserAddress());
            userMo.setBirth(userVo.getBirth());
            userMo.setNickName(userVo.getNickName());
            userMapper.updateById(userMo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 设置用户头像*
     *
     * @param file
     *            file
     * @param openId
     *            openId
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo setPortrait(MultipartFile file, String openId) {
        Map<String, String> map = new HashMap<>();
        try {
            AliyunClient aliyunClient = getAliyunClient();
            String url =
                aliyunClient.uploadObject2OSS(new ByteArrayInputStream(file.getBytes()), file.getOriginalFilename());
            map.put("url", UserConstant.HTTPS + url);
            UserMo userMo = userMapper.selectByOpenId(openId);
            userMo.setPortraitAddress(UserConstant.HTTPS + url);
            userMapper.updateById(userMo);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return ResultUtil.data(map);
    }

    /**
     * 生成随机uid
     *
     * @return
     */
    private String randomNumber() {
        Random random = new Random();
        String openId =
            (random.nextInt(900) + 100) + "-" + (random.nextInt(900) + 100) + "-" + (random.nextInt(900) + 100);
        UserMo userMo = userMapper.selectByOpenId(openId);
        if (userMo != null) {
            return randomNumber();
        }
        return openId;
    }

    /**
     * 生成邀请码*
     *
     * @return String 邀请码
     */
    public String genRandomNum() {
        int maxNum = 36;
        int i;
        int count = 0;
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < 6) {
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

    /**
     * 从系统设置中获取阿里云*
     *
     * @return AliyunClient AliyunClient
     */
    public AliyunClient getAliyunClient() {
        final String folder = sysConfigMapper.selectByKey(SysConfigConstant.FOLDER);
        final String access_key_id = sysConfigMapper.selectByKey(SysConfigConstant.ACCESS_KEY_ID);
        final String bucket = sysConfigMapper.selectByKey(SysConfigConstant.Bucket);
        final String access_key_secret = sysConfigMapper.selectByKey(SysConfigConstant.ACCESS_KEY_SECRET);
        final String endpoint = sysConfigMapper.selectByKey(SysConfigConstant.ENDPOINT);
        return new AliyunClient(endpoint, access_key_id, access_key_secret, bucket, folder);
    }

}
