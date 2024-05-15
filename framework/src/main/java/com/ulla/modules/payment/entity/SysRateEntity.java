package com.ulla.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 系统汇率表
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-03-04 10:41:52
 */
@Data
@TableName("sys_rate")
public class SysRateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 法币币种
	 */
	private String symbol;
	/**
	 * 币种说明:{USD-USD,RUB-俄罗斯卢布（应该是俄语写的，跟切换语言的功能一样）}
	 */
	private String symbolExplain;
	/**
	 * 符号
	 */
	private String unit;
	/**
	 * 地区缩写
	 */
	private String acronym;
	/**
	 * 国家/地区
	 */
	private String nationExplain;
	/**
	 * 比率 1:??
	 */
	private BigDecimal rate;
	/**
	 * 支持购买币种(BTC，ETH) ，号分割
	 */
	private String supportSymbol;
	/**
	 * 排序
	 */
	private Integer fieldSort;
	/**
	 * 状态：1启用 2禁用
	 */
	private Long fieldStatus;
	/**
	 * 三方采集状态： 1启用 2禁用
	 */
	private Long gatherStatus;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 是否热门： 1热门 2不热门
	 */
	private Long hotStatus;
	/**
	 * 法币图标
	 */
	private String logo;
	/**
	 * 创建用户
	 */
	private String createUser;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 更新用户
	 */
	private String updateUser;

}
