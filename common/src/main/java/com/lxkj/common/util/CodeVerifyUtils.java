package com.lxkj.common.util;

import java.util.Random;

/**
 * @Author apple
 * @Date 2018/3/23 12:10
 * @Description:
 * @Modified by:
 */
public class CodeVerifyUtils {

    /**
     * 生成指定长度的验证码
     *
     * @param verificationCodeLength
     * @return String
     * @author Q
     * @since 2016-09-6
     */
    public static final String createVerificationCode(int verificationCodeLength) {
        //    所有候选组成验证码的字符，可以用中文
        String[] verificationCodeArrary = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String verificationCode = "";
        Random random = new Random();
        //此处是生成验证码的核心了，利用一定范围内的随机数做为验证码数组的下标，循环组成我们需要长度的验证码，做为页面输入验证、邮件、短信验证码验证都行
        for (int i = 0; i < verificationCodeLength; i++) {
            verificationCode += verificationCodeArrary[random.nextInt(verificationCodeArrary.length)];
        }
        return verificationCode;
    }

    public static void main(String[] args) {
        System.out.println(createVerificationCode(6));
    }
}
