package com.ulla.modules.business.vo;

import lombok.Data;

/**
 * @Description 结算时间点配置
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
public class SectionQuotationVo {

    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long endTime;

}
