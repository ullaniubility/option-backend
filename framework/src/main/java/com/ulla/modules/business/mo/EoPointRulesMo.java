package com.ulla.modules.business.mo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseIdEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@TableName("biz_eo_point_rules")
@ApiModel(value = "用户的eo积分日志", description = "用户的eo积分日志")
public class EoPointRulesMo extends BaseIdEntity {

    private Integer start;

    private Integer end;

    private Integer isDisable;

    private BigDecimal rewardPoints;

    private Long createTime;

    private Integer deleteFlag;


}
