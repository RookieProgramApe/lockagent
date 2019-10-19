package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author Hu Xiao
 * @since 2018-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysUser对象", description = "系统用户表")
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "头像")
    @TableField("avatarUrl")
    private String avatarUrl;


    @ApiModelProperty(value = "用户名")
    @TableField("userName")
    private String userName;


    @ApiModelProperty(value = "密码")
    @TableField("passWord")
    private String passWord;


    @ApiModelProperty(value = "盐值")
    @TableField("loginSalt")
    private String loginSalt;


    @ApiModelProperty(value = "姓名")
    @TableField("realName")
    private String realName;


    @ApiModelProperty(value = "是否禁用 0:否 1:是")
    @TableField("enableStatus")
    private Integer enableStatus;


    @TableField("createTime")
    private Date createTime;


    @ApiModelProperty(value = "手机号")
    @TableField("mobile")
    private String mobile;


    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;


    @ApiModelProperty(value = "性别")
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "0普通权限 1超级权限")
    @TableField("isauth")
    private Integer isauth;


    @TableField(exist = false)
    private List<SysResource> sysResourceList;
    @TableField(exist = false)
    private List<SysRole> roleList;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
