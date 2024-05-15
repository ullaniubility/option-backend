package com.ulla.modules.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.qo.UserAdminQo;
import com.ulla.modules.auth.qo.UserQo;
import com.ulla.modules.auth.vo.UserPageVo;
import com.ulla.modules.business.vo.MemberLoginVo;

/**
 * @Description {用户mapper}
 * @author {clj}
 * @since {2023-2-8}
 */
public interface UserMapper extends BaseMapper<UserMo> {

    @Select("select * from biz_user where open_id = #{openId} and (delete_flag != 1 or delete_flag is null)")
    UserMo selectByOpenId(String openId);

    @Select("select uid from biz_user where open_id = #{openId} and (delete_flag != 1 or delete_flag is null)")
    Long getUserIdByOpenId(String openId);

    @Select("select * from biz_user where mail = #{mail} and (delete_flag != 1 or delete_flag is null)")
    List<UserMo> selectByMail(String mail);

    @Select("select * from biz_user where fingerprint = #{fingerprint} and (delete_flag != 1 or delete_flag is null) and type = 0")
    UserMo selectDemoAccount(String fingerprint);

    @Select("select * from biz_user where mail = #{mail} and password = #{password} and (delete_flag != 1 or delete_flag is null)")
    UserMo selectByLogin(String mail, String password);

    @Select("select * from biz_user where open_id = #{openId}  and (delete_flag != 1 or delete_flag is null)")
    UserQo selectByOpenIdForShow(String openId);

    @Select("SELECT w.address as wallectAddress,a.nick_name,a.uid, a.open_id AS openId, a.mail, a.phone, b.realBalance, b.bonusBalance, "
        + "b.virtuallyBalance, a.kyc_status  AS kycStatus, a.delete_flag AS deleteFlag, "
        + "a.create_time AS createTime, a.area, a.type, CONCAT( a.first_name, a.last_name ) AS `name`, "
        + "COUNT(c.id) AS orderCount, COALESCE ( SUM( c.order_amount ), 0.00 ) AS orderCountAmount FROM biz_user AS a LEFT JOIN biz_user_wallet_connect w ON w.uid=a.uid LEFT JOIN (SELECT user_id, "
        + "SUM(IF(type = 1, balance, 0)) AS realBalance, SUM(IF(type = 0, balance, 0)) AS bonusBalance, "
        + "SUM(IF(type = 3, balance, 0)) AS virtuallyBalance FROM biz_balance GROUP BY user_id) AS b ON a.uid = b.user_id "
        + "LEFT JOIN biz_order AS c ON a.uid = c.uid AND c.`status` = 2 ${ew.customSqlSegment} GROUP BY a.uid ORDER BY a.create_time DESC")
    IPage<UserPageVo> userPageAdmin(Page<UserPageVo> page,
        @Param(Constants.WRAPPER) QueryWrapper<UserAdminQo> queryWrapper);

    @Select("SELECT o.order_amount,o.uid,o.price,o.ip_address,u.open_id FROM biz_order o LEFT JOIN biz_user u ON o.uid=u.uid WHERE o.type=1 ORDER BY o.order_time DESC LIMIT 10")
    List<MemberLoginVo> getList();

    @Select("select COUNT(uid) from biz_user WHERE (delete_flag != 1 or delete_flag is null)")
    int getTotalMember();

    @Select("select COUNT(uid) from biz_user WHERE create_time BETWEEN #{todayEnterBeginTime} AND #{todayEnterEndTime} and (delete_flag != 1 or delete_flag is null)")
    int addMember(long todayEnterBeginTime, Long todayEnterEndTime);
}
