package com.ulla.modules.assets.service.impl;

import static com.ulla.constant.BinanceConstant.ADDRESS_NET;
import static com.ulla.constant.NumberConstant.ONE;
import static com.ulla.constant.NumberConstant.ZERO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.PageUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mapper.AddressMapper;
import com.ulla.modules.assets.mo.AddressMo;
import com.ulla.modules.assets.service.AddressService;
import com.ulla.modules.assets.vo.AddressParameterVO;
import com.ulla.modules.assets.vo.AddressVo;

/**
 * <p>
 * 用户钱包地址表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressMo> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ResultMessageVo importAddress(List<AddressVo> addressVos) {
        if (CollectionUtils.isEmpty(addressVos)) {
            return ResultUtil.error(4002, "Import data as 0");
        }
        List<AddressMo> list = addressVos.stream().map(addressVo -> {
            AddressMo address = new AddressMo();
            address.setAddress(addressVo.getAddress());
            address.setNet(addressVo.getNet().toUpperCase());
            address.setState(ZERO);
            address.setScanFlag(ZERO);
            return address;
        }).collect(Collectors.toList());
        boolean b = saveBatch(list);
        if (b) {
            return ResultUtil.success();
        } else {
            return ResultUtil.error(ResultCodeEnums.FILE_IMPORT_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo selectAddress(Integer isAll) {
        LambdaQueryWrapper<AddressMo> wrapper = new LambdaQueryWrapper<AddressMo>().eq(AddressMo::getState, ONE)
            .eq(AddressMo::getType, ONE).isNotNull(AddressMo::getUserId);
        if (isAll.equals(ZERO)) {
            wrapper.eq(AddressMo::getScanFlag, ZERO);
        }
        // 查询已绑定userid和未进入扫描的地址
        List<AddressMo> list = list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(addressMo -> addressMo.setScanFlag(ONE));
            updateBatchById(list);
        }
        return ResultUtil.data(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo bindAddress(Integer type, Long userId) {
        List<String> nets = new ArrayList<>(ADDRESS_NET);
        List<AddressMo> queryList = list(new LambdaQueryWrapper<AddressMo>().eq(AddressMo::getUserId, userId)
            .eq(AddressMo::getType, type).eq(AddressMo::getState, ONE));
        List<AddressMo> updateList = new ArrayList<>(6);
        if (CollectionUtils.isEmpty(queryList)) {
            nets.forEach(net -> {
                AddressMo randomAddress = getRandomAddress(net);
                randomAddress.setUserId(userId);
                randomAddress.setState(1);
                randomAddress.setType(type);
                updateList.add(randomAddress);
                queryList.add(randomAddress);
            });
        } else {
            // 查询的地址必须有6条，缺少哪条链的地址就绑定哪条地址
            boolean allMatch = queryList.stream().allMatch(addressMo -> nets.contains(addressMo.getNet()));
            if (!allMatch) {
                nets.removeAll(queryList.stream().map(AddressMo::getNet).collect(Collectors.toList()));
                nets.forEach(net -> {
                    AddressMo randomAddress = getRandomAddress(net);
                    randomAddress.setUserId(userId);
                    randomAddress.setState(ONE);
                    randomAddress.setType(type);
                    updateList.add(randomAddress);
                    queryList.add(randomAddress);
                });
            }
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            updateBatchById(updateList);
        }
        return ResultUtil.data(queryList);
    }

    @Override
    public AddressMo getRandomAddress(String net) {
        return getOne(new LambdaQueryWrapper<AddressMo>().eq(AddressMo::getNet, net).eq(AddressMo::getState, ZERO)
            .last("limit 1"));
    }

    /**
     * 钱包地址池列表查询
     * 
     * @param vo
     *            列表查询参数
     * @return
     */
    public ResultMessageVo addressListByParamer(AddressParameterVO vo) {
        Integer pages = vo.getPage();
        Integer page = (pages - 1) * vo.getPageSize();
        vo.setPage(page);
        List<AddressMo> list = addressMapper.getAddressList(vo);
        Integer listCount = addressMapper.getAddressListCount(vo);
        PageUtils pageUtils = new PageUtils(list, listCount, vo.getPageSize(), pages);
        return ResultUtil.data(pageUtils);
    }
}
