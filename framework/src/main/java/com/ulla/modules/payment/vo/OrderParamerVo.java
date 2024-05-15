package com.ulla.modules.payment.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderParamerVo {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "订单副id")
    private String subOrderId;

    @NotBlank(message = "渠道唯一的标识必填!")
    @ApiModelProperty(value = "渠道标识-渠道唯一的标识")
    private String channelIdent;

    @ApiModelProperty(value = "订单类型 买或卖  buy sell")
    private String type;

    @ApiModelProperty(value = "金额")
    private BigDecimal value;

    @ApiModelProperty(value = "输入框的金额")
    private BigDecimal inputValue;

    @ApiModelProperty(value = "金额种类-USD等")
    private String currency;

    @ApiModelProperty(value = "到账地址")
    private String toAddress;

    @ApiModelProperty(value = "到账金额")
    private String tokenAmount;

    @ApiModelProperty(value = "到账类型-ETH等")
    private String net;

    @ApiModelProperty(value = "合约地址-代币的智能合约地址-会变动")
    private String contract;

    @ApiModelProperty(value = "永久代码-合约地址或者到账地址变更这个代码不会变更")
    private String symbol;

    @ApiModelProperty(value = "创建人-可以为空")
    private String userId;

    @ApiModelProperty(value = "合作方id")
    private String businessId;

    @ApiModelProperty(value = "合作方储存的该用户的id")
    private String businessUserId;

    @ApiModelProperty(value = "渠道方储存的该用户的id")
    private String channelUserId;

    @ApiModelProperty(value = "创建订单的时间")
    private Date createdTime;

    @ApiModelProperty(value = "换算价格，即虚拟币的实时价格")
    private BigDecimal conversionPrice;

    @ApiModelProperty(value = "是否需要商城发货-0不需要，1需要")
    private Integer deliverGoods;

    @ApiModelProperty(value = "主题色")
    private String themeColorVal;

    @ApiModelProperty(value = "语言")
    private String localeId;

    @ApiModelProperty(value = "订单状态变更后回调URL")
    private String callUrl;

    @ApiModelProperty(value = "订单支付后(仅支付，并不是完成)跳转的URL")
    private String reUrl;

    @ApiModelProperty(value = "用户邮箱")
    private String userEmail;

    //----------查询条件----------

    @ApiModelProperty("当前页")
    @TableField(exist = false)
    private Integer page = 1;
    @ApiModelProperty("每页大小")
    @TableField(exist = false)
    private Integer limit = 10;
    @ApiModelProperty("查询开始时间")
    private String startTime;
    @ApiModelProperty("查询结束时间")
    private String endTime;
    @ApiModelProperty("订单状态")
    private Integer orderState;
    @ApiModelProperty("金额区间-最小金额")
    private String valueMin;
    @ApiModelProperty("金额区间-最大金额")
    private String valueMax;
    @ApiModelProperty(value = "订单id集合")
    private List<String> orderIdList;
}
