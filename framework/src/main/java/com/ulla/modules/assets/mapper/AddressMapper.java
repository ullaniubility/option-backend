package com.ulla.modules.assets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.assets.mo.AddressMo;
import com.ulla.modules.assets.vo.AddressParameterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户钱包地址表 Mapper 接口
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Mapper
public interface AddressMapper extends BaseMapper<AddressMo> {

    List<AddressMo> getAddressByUserId(@Param("address")  String address,@Param("userId")  Long userId);


    /**
     * 地址池列表查询
     * @param vo 查询参数
     * @return
     */
    public List<AddressMo> getAddressList(AddressParameterVO vo);
    public Integer getAddressListCount(AddressParameterVO vo);


}
