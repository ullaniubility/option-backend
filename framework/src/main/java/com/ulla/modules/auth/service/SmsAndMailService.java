package com.ulla.modules.auth.service;

import com.ulla.constant.VerificationConstant;

/**
 * @Description {SmsAndMailService}
 * @author {clj}
 * @since {2023-2-15}
 */
public interface SmsAndMailService {

    boolean sendSms(String phone, String phoneCode, VerificationConstant.Verify_Type type);

    boolean sendMail(String mailbox, VerificationConstant.Verify_Type type);
}
