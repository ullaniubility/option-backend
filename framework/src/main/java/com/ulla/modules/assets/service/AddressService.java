package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.AddressMo;
import com.ulla.modules.assets.vo.AddressParameterVO;
import com.ulla.modules.assets.vo.AddressVo;

import java.util.List;

/**
 * <p>
 * 用户钱包地址表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
public interface AddressService extends IService<AddressMo> {

    /**
     * 导入地址
     * @param addressVos
     * @return
     */
    ResultMessageVo importAddress(List<AddressVo> addressVos);

    /**
     * 查询未进入扫链的地址
     * @param isAll
     * @return
     */
    ResultMessageVo selectAddress(Integer isAll);

    /**
     * 绑定地址并返回地址
     * @param type
     * @param userId
     * @return
     */
    ResultMessageVo bindAddress(Integer type,Long userId);

    /**
     * 根据链获取随机的一个未绑定的地址
     * @param net
     * @return
     */
    AddressMo getRandomAddress(String net);

    /**
     * 钱包地址池列表查询
     * @param vo  列表查询参数
     * @return
     */
    public ResultMessageVo addressListByParamer(AddressParameterVO vo);
}
