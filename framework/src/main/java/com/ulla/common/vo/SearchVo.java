package com.ulla.common.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 日期搜索参数
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVo implements Serializable {

    @ApiModelProperty(value = "起始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    public Date getConvertStartDate() {
        if (StringUtils.isEmpty(startDate)) {
            return null;
        }
        return DateUtil.toDate(startDate, DateUtil.STANDARD_DATE_FORMAT);
    }

    public Date getConvertEndDate() {
        if (StringUtils.isEmpty(endDate)) {
            return null;
        }
        // 结束时间等于结束日期+1天 -1秒，
        Date date = DateUtil.toDate(endDate, DateUtil.STANDARD_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        calendar.set(Calendar.SECOND, -1);
        return calendar.getTime();
    }
}
