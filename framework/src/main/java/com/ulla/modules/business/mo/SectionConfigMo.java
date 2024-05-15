package com.ulla.modules.business.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Description 结算时间点配置
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qa_section_config")
@ToString
public class SectionConfigMo extends BaseEntity {

    /**
     * 结算间隔时间（秒）
     */
    @TableField("interval_time")
    private Integer intervalTime;

    /**
     * 展示时间结算点
     */
    @TableField("display_number")
    private Integer displayNumber;

    /**
     * 每天最多结算次数
     */
    @TableField("settle_counts")
    private Integer settleCounts;

    /**
     * 距离结算点多少秒内，交易订单进入下一个结算点
     */
    @TableField("next_time")
    private Long nextTime;

}
