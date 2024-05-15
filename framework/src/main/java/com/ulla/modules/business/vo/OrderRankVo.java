package com.ulla.modules.business.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 订单排名
 * @author zhuyongdong
 * @since 2023-04-04 13:26:49
 */
@Data
public class OrderRankVo {

    @ApiModelProperty(value = "排名时间")
    private String rankTime;

    @ApiModelProperty(value = "排名时间戳")
    private Long rankTimeStamp;

    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;

    @ApiModelProperty(value = "交易量")
    private Integer orderTotalAmount;

    @ApiModelProperty(value = "用户盈利排行榜")
    private List<UserRank> userRank;

    @Data
    public static class UserRank {

        @ApiModelProperty(value = "昵称")
        private String name;

        @ApiModelProperty(value = "交易对")
        private String pairs;

        @ApiModelProperty(value = "分时盈利")
        private Integer benefit;

    }

}
