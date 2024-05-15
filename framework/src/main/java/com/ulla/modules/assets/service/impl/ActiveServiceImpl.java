package com.ulla.modules.assets.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.PageUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.constant.NumberConstant;
import com.ulla.modules.assets.mapper.ActiveCouponMapper;
import com.ulla.modules.assets.mapper.ActiveMapper;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.service.ActiveCouponService;
import com.ulla.modules.assets.service.ActiveService;
import com.ulla.modules.assets.vo.ActiveCouponVO;
import com.ulla.modules.assets.vo.ActiveParameterVO;
import com.ulla.modules.assets.vo.ActiveVo;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 促销活动表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ActiveServiceImpl extends ServiceImpl<ActiveMapper, ActiveMo> implements ActiveService {

    @Lazy
    @Resource
    ActiveCouponService activeCouponService;

    @Autowired
    private ActiveMapper activeMapper;

    @Autowired
    private ActiveCouponMapper activeCouponMapper;

    @Override
    public ResultMessageVo saveActiveAndSyncCreateCoupon(ActiveVo activeVo) {
        log.error("线程名称：{}", Thread.currentThread().getName());
        if (activeVo.getEndTime() < activeVo.getBeginTime()) {
            throw new ServiceException("结束时间不得早于开始时间");
        }
        if (activeVo.getAmountRangeEnd() < activeVo.getAmountRangeBegin()) {
            throw new ServiceException("金额区间错误");
        }
        ActiveMo activeMo = new ActiveMo();
        BeanUtil.copyProperties(activeVo, activeMo);
        save(activeMo);
        activeVo.setActiveId(activeMo.getId());
        CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
            activeCouponService.createCoupon(activeVo);
        }));
        return ResultUtil.data("促销券批量生成中，请稍后查看");
    }

    /**
     * 促销活动列表查询
     *
     * @param vo
     *            列表查询参数
     * @return
     */
    public ResultMessageVo activeListByParamer(ActiveParameterVO vo) {
        Integer pages = vo.getPage();
        Integer page = (pages - 1) * vo.getPageSize();
        vo.setPage(page);

        List<ActiveMo> list = activeMapper.getActiveList(vo);
        Integer listCount = activeMapper.getActiveListCount(vo);
        PageUtils pageUtils = new PageUtils(list, listCount, vo.getPageSize(), pages);
        return ResultUtil.data(pageUtils);
    }

    /**
     * 促销活动管理 - 促销活动修改接口
     *
     * @param mo
     *            促销活动修改参数
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public ResultMessageVo updateActive(ActiveMo mo) {
        Calendar cal = Calendar.getInstance();
        Long time = cal.getTimeInMillis();
        Long id = mo.getId();
        if (id != null) {
            List<ActiveMo> list = activeMapper.getActiveListByParamer(id, time);
            if (CollectionUtils.isNotEmpty(list)) {
                return ResultUtil.error(500, "The activity does not exist or has already started. Please check!");
            }
        }

        ActiveMo active = activeMapper.selectById(id);
        if (mo.getBeginTime() != null && time > active.getBeginTime()) {
            return ResultUtil.error(500, "The current time has exceeded the activity start time!");
        }
        if ((mo.getEndTime() != null && mo.getBeginTime() != null) && mo.getEndTime() < mo.getBeginTime()) {
            return ResultUtil.error(500, "The end time must not be earlier than the start time!");
        }
        if ((mo.getAmountRangeEnd() != null && mo.getAmountRangeBegin() != null)
            && mo.getAmountRangeEnd() < mo.getAmountRangeBegin()) {
            return ResultUtil.error(500, "Amount range error!");
        }
        Integer activeNum = active.getNum();// 促销活动对应的促销卷数量
        Integer num = mo.getNum();// 需要修改的促销活动对应的促销卷数量

        // 当促销卷数量修改时，删除原有促销活动绑定的促销卷并且重新生成新的促销卷
        if (num != null && num != activeNum) {
            ActiveVo activeVo = new ActiveVo();
            activeVo.setNum(num);
            activeVo.setActiveId(id);
            // 删除对应促销活动绑定的促销卷
            activeCouponMapper.deleteBatch(id);
            // BeanUtil.copyProperties(active, activeVo);
            // 从新生成新的促销卷，并且绑定到促销活动上
            CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
                activeCouponService.createCoupon(activeVo);
            }));
        }
        activeMapper.updateById(mo);
        return ResultUtil.success();
    }

    /**
     * 促销活动管理 - 促销活动详情获取接口
     *
     * @param id
     *            促销活动Id
     * @return
     */
    public ResultMessageVo getActiveInfoById(Long id) {
        ActiveMo active = activeMapper.selectById(id);
        return ResultUtil.data(active);
    }

    /**
     * 促销活动获取启用列表接口
     *
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo activeList() {
        QueryWrapper<ActiveMo> wrapper = new QueryWrapper<>();
        wrapper.eq("state", NumberConstant.ONE);
        return ResultUtil.data(activeMapper.selectList(wrapper));
    }

    /**
     * 促销码列表查询
     *
     * @param vo
     *            列表查询参数
     * @return
     */
    public ResultMessageVo activeCouponListByParamer(ActiveParameterVO vo) {
        Integer pages = vo.getPage();
        Integer page = (pages - 1) * vo.getPageSize();
        vo.setPage(page);

        List<ActiveCouponVO> list = activeCouponMapper.getActiveCouponList(vo);
        Integer listCount = activeCouponMapper.getActiveCouponListCount(vo);
        PageUtils pageUtils = new PageUtils(list, listCount, vo.getPageSize(), pages);
        return ResultUtil.data(pageUtils);
    }

}
