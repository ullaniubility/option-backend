package com.ulla.constant;

/**
 * @Description {验证码相关参数}
 * @author {clj}
 * @since {2023-2-8}
 */
public class VerificationConstant {

    public enum Verify_Type {

        REGISTER("REGISTER"), // 注册
        RESET("RESET"), // 忘记密码/重置密码
        MAIL("MAIL"), // 邮箱
        PHONE("PHONE"), // 手机
        TOPUP("TOP_UP"), // 入金
        WITHDRAWAL("WITHDRAWAL"); // 出金

        private String verifyType;

        Verify_Type(String verifyType) {
            this.verifyType = verifyType;
        }

        public String getVerifyType() {
            return verifyType;
        }

        public void setVerifyType(String verifyType) {
            this.verifyType = verifyType;
        }

        @Override
        public String toString() {
            return verifyType;
        }
    }
}
