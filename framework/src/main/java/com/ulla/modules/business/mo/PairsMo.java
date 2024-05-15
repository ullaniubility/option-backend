package com.ulla.modules.business.mo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_pairs")
@ApiModel(value = "Pairs对象", description = "")
public class PairsMo implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("交易对名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("头像路径")
    @TableField("url")
    private String url;

    @ApiModelProperty("最高价")
    @TableField("highest_price")
    private BigDecimal highestPrice;

    @ApiModelProperty("最低价")
    @TableField("howest_price")
    private BigDecimal howestPrice;

    @ApiModelProperty("当前价")
    @TableField("current_price")
    private BigDecimal currentPrice;

    @ApiModelProperty("1启用0禁用")
    @TableField("status")
    private Integer status;
}
