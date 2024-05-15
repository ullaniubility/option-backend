package com.ulla.modules.assets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.assets.mo.ActiveCouponMo;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.vo.ActiveCouponVO;
import com.ulla.modules.assets.vo.ActiveParameterVO;
import com.ulla.modules.assets.vo.CouponVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 促销优惠券表 Mapper 接口
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Mapper
public interface ActiveCouponMapper extends BaseMapper<ActiveCouponMo> {

    /**
     * 根据code和amount获取coupon
     * @param couponCode
     * @param amount
     * @return
     */
    CouponVo getByCodeAndAmount(@Param("code") String couponCode, @Param("amount") Integer amount);

    long couponUnbindOrder(@Param("id")Long couponId);


    /**
     * 促销码列表查询
     * @param vo 查询参数
     * @return
     */
    public List<ActiveCouponVO> getActiveCouponList(ActiveParameterVO vo);
    public Integer getActiveCouponListCount(ActiveParameterVO vo);


    /**
     * 根据促销活动ID删除对应活动关联的促销码
     * @param activeId
     * @return
     */
    public Integer deleteBatch(@Param("activeId") Long activeId);
}
