package com.ulla.modules.auth.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("biz_user_authority")
public class UserAuthorityMo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @TableField("uid")
    private Long uid;
    @TableField("allowe_withdrawal")
    private Integer alloweWithdrawal;

}
