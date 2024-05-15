package com.ulla.modules.business.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_privacy")
@ApiModel(value = "Privacy对象", description = "")
public class PrivacyMo implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Long createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Long updateTime;

    @ApiModelProperty("编辑人id")
    @TableField("operator_uid")
    private Long operatorUid;

    @ApiModelProperty("排序:1置顶0正常")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("0禁用1启用")
    @TableField("`status_type`")
    private Integer statusType;

    @ApiModelProperty("0是用户注册时的隐私政策，1是用户入金时的支付政策")
    @TableField("`form`")
    private Integer form;

}
