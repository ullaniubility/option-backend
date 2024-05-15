package com.ulla.modules.business.mo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;
import com.ulla.mybatis.BaseIdEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@TableName("biz_eo_point_for_user")
@ApiModel(value = "用户的eo积分表", description = "用户的eo积分表")
public class EoPointForUserMo extends BaseIdEntity {

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * eo积分
     */
    private BigDecimal eoPoint = new BigDecimal(0);

    /**
     * 总交易额
     */
    private BigDecimal allAmount = new BigDecimal(0);

    private Long createTime;

    private Long updateTime;

}
