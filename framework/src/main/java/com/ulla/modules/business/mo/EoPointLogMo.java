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
@TableName("biz_eo_point_log")
@ApiModel(value = "用户的eo积分日志", description = "用户的eo积分日志")
public class EoPointLogMo extends BaseIdEntity {

    private Long uid;

    /**
     * 种类： 0，交易奖励  1.使用扣除
     */
    private Integer type;

    /**
     * business订单流水号
     */
    private String businessNo;

    /**
     * 操作变化的eo积分
     */
    private BigDecimal eoPoint;

    private Long createTime;

    private Integer deleteFlag;


}
