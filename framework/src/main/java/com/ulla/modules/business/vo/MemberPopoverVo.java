package com.ulla.modules.business.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class MemberPopoverVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 新用户弹窗：1开启，0关闭
     */
    private Integer isDisable = 0;

    /**
     * 新用户时间设置--多少时间内生成的用户算新用户
     */
    private Integer hours = 1;

    /**
     * 新用户弹窗展示内容
     */
    private String content = "新用户你好";
}
