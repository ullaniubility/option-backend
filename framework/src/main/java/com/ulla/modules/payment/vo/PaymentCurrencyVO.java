package com.ulla.modules.payment.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入金支付功能支持的币种
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-03-13 16:47:19
 */
@Data
public class PaymentCurrencyVO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 币种符号
	 */
	private String symbol;
	/**
	 * 币种全称
	 */
	private String fullName;
	/**
	 * 图标链接
	 */
	private String logoUrl;
	/**
	 * 通过人工干预统计的交易量(USD)
	 */
	private BigDecimal volumeUsd;
	/**
	 * 1 已上线  2 已下线  3 测试中
	 */
	private Integer currencyStatus;
	/**
	 * 币种市值
	 */
	private BigDecimal marketCapusd;
	/**
	 * 流通量
	 */
	private BigDecimal availableSupply;
	/**
	 * 发行总量
	 */
	private BigDecimal totalSupply;
	/**
	 * 最大发行量
	 */
	private BigDecimal maxSupply;
	/**
	 * 官网链接
	 */
	private String websiteUrl;
	/**
	 * 区块浏览器链接
	 */
	private String explorerUrls;
	/**
	 * 白皮书
	 */
	private String whitePaperUrls;
	/**
	 * Github
	 */
	private String githubId;
	/**
	 * Twitter
	 */
	private String twitterId;
	/**
	 * FaceBook
	 */
	private String facebookId;
	/**
	 * Telegram
	 */
	private String telegramId;
	/**
	 * 核心算法
	 */
	private String algoritHm;
	/**
	 * 激励机制
	 */
	private String proof;
	/**
	 * 跨链信息
	 */
	private String platforms;
	/**
	 * 上市时间
	 */
	private Date issueDate;
	/**
	 * 合约地址
	 */
	private String contractAddress;
	/**
	 * 是否忽略市值(0 否 1 是)
	 */
	private String ignores;
	/**
	 * 是否法定货币（0 否 1 是）
	 */
	private String fiat;
	/**
	 * 币种介绍
	 */
	private String details;
	/**
	 * 币类型 1: 公链主币  2: ERC 20类代币  3: OMNI 类代币
	 */
	private Integer coinType;
	/**
	 * 接口JSON数组
	 */
	private String abi;
	/**
	 * 0 否 1 是
	 */
	private Integer isDelete;
	/**
	 * 类型(ETH BTC TRX MATIC HECO BSC)
	 */
	private String net;
	/**
	 * 类型名称(Ethereum, Bitcoin, Tron, Polygon, HECO, Binance Smart Chain)
	 */
	private String netName;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 修改时间
	 */
	private Date updateDate;
	/**
	 * 精度
	 */
	private Integer decimalNumber;
	/**
	 * 是否允许兑换 0 不允许 1 允许兑换
	 */
	private String isExchange;
	/**
	 * 是否允许购买 0 不允许 1 允许购买
	 */
	private Integer isBuy;

	/**
	 * 是否支持 0-支持 1-不支持
	 */
	private Integer supported;
}
