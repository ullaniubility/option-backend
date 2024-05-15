package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.vo.ActiveParameterVO;
import com.ulla.modules.assets.vo.ActiveVo;

/**
 * <p>
 * 促销活动表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
public interface ActiveService extends IService<ActiveMo> {

    /**
     * 保存活动异步生成优惠券
     * @param activeVo
     * @return
     */
    ResultMessageVo saveActiveAndSyncCreateCoupon(ActiveVo activeVo);

    /**
     * 促销活动列表查询
     * @param vo  列表查询参数
     * @return
     */
    public ResultMessageVo activeListByParamer(ActiveParameterVO vo);

    /**
     * 促销码列表查询
     * @param vo  列表查询参数
     * @return
     */
    public ResultMessageVo activeCouponListByParamer(ActiveParameterVO vo);



    /**
     * 促销活动管理 - 促销活动修改接口
     * @param mo 促销活动修改参数
     * @return
     */
    public ResultMessageVo updateActive(ActiveMo mo);


    /**
     * 促销活动管理 - 促销活动详情获取接口
     * @param id 促销活动Id
     * @return
     */
    public ResultMessageVo getActiveInfoById(Long id);

    ResultMessageVo activeList();
}
