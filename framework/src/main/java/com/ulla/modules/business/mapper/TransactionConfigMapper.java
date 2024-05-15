package com.ulla.modules.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.business.mo.TransactionConfigMo;

public interface TransactionConfigMapper extends BaseMapper<TransactionConfigMo> {

    @Select("select * from  qa_transaction_config")
    List<TransactionConfigMo> getList();
}
