package com.ulla.modules.assets.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.PageUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.modules.assets.mapper.BalanceLogMapper;
import com.ulla.modules.assets.mo.AddressMo;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.service.BalanceLogService;
import com.ulla.modules.assets.vo.AddressParameterVO;
import com.ulla.modules.assets.vo.BalanceLogParameterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.ulla.constant.NumberConstant.ZERO;
import static com.ulla.constant.NumberConstant.ZERO_L;

/**
 * <p>
 * 用户资产流水表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Service
public class BalanceLogServiceImpl extends ServiceImpl<BalanceLogMapper, BalanceLogMo> implements BalanceLogService {

    @Autowired
    private BalanceLogMapper balanceLogMapper;

    @Override
    public void createLog(BalanceLogMo balanceLogMo, Long userId) {
        //交易和提现的情况下会有负数的余额产生
        if(balanceLogMo.getAmount().compareTo(BigDecimal.ZERO) < ZERO){
            balanceLogMo.setFromUserId(ZERO_L);
            balanceLogMo.setToUserId(userId);
        }

        if(balanceLogMo.getAmount().compareTo(BigDecimal.ZERO) > ZERO){
            balanceLogMo.setFromUserId(userId);
            balanceLogMo.setToUserId(ZERO_L);
        }
        balanceLogMo.setAmount(balanceLogMo.getAmount().abs());
        save(balanceLogMo);
    }



    /**
     * 资金流水表列表查询
     * @param vo  列表查询参数
     * @return
     */
    public ResultMessageVo balanceLogListByParamer(BalanceLogParameterVO vo) {
        Integer pages = vo.getPage();
        Integer page = (pages - 1) * vo.getPageSize();
        vo.setPage(page);
        List<BalanceLogMo> list = balanceLogMapper.getBalanceLogList(vo);
        Integer listCount = balanceLogMapper.getBalanceLogListCount(vo);
        PageUtils pageUtils = new PageUtils(list, listCount, vo.getPageSize(), pages);
        return ResultUtil.data(pageUtils);
    }


}
