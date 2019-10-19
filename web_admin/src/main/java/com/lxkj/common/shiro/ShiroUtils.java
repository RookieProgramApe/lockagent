package com.lxkj.common.shiro;

import com.lxkj.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

public class ShiroUtils {

    /**
     * 获取登录信息
     *
     * @return
     */
    public static SysUser getSysUser() {
        SysUser userAcct = (SysUser) SecurityUtils.getSubject().getPrincipal();
        return userAcct;
    }

    /**
     * 获取用户ID
     * @return
     */
    public static String getUserId() {
        return  getSysUser().getId();
    }

    /**
     * 是否有某角色
     *
     * @param roleCode
     * @return
     */
    public static Boolean hasRole(String roleCode) {
        Boolean result = SecurityUtils.getSubject().hasRole(roleCode);
        return result;
    }

    public static String getPhone(String phone) {
        if(StringUtils.isNotBlank(phone)){
            if(ShiroUtils.getSysUser().getIsauth()==1){
                return phone;
            }else{
                return phone.length()>7?phone.substring(0,3)+"****"+phone.substring(phone.length()-4):phone;
            }
        }else{
            return "";
        }
    }
    public static String getIdCard(String phone) {
        if(StringUtils.isNotBlank(phone)){
            if(ShiroUtils.getSysUser().getIsauth()==1){
                return phone;
            }else{
                return phone.length()>7?phone.substring(0,3)+"********"+phone.substring(phone.length()-4):phone;
            }
        }else{
            return "";
        }
    }

}
