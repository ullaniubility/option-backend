package com.ulla.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 入金订单补发理由信息表
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-03-21 14:21:35
 */
@Data
@TableName("reason_reissue_info")
public class ReasonReissueInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long id;
	/**
	 * 交易订单Id(法币购买第三方支付订单Id，数字币生成订单Id)
	 */
	private String orderId;
	/**
	 * 补发理由
	 */
	private String reasonInfo;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
