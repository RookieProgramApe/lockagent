package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Member对象", description = "会员")
public class Member extends Model<Member> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID(token)")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "手机号码")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty(value = "0:普通会员 1代理商")
    @TableField("isretailer")
    private Integer isretailer;


    @ApiModelProperty(value = "头像")
    @TableField("avatar")
    private String avatar;


    @ApiModelProperty(value = "代理商ID",hidden = true)
    @TableField("retailer_id")
    private String retailerId;


    @ApiModelProperty(value = "UNION ID",hidden = true)
    @TableField("union_id")
    private String unionId;


    @ApiModelProperty(value = "openId")
    @TableField("open_id")
    private String openId;


    @ApiModelProperty(value = "昵称")
    @TableField("nickname")
    private String nickname;


    @ApiModelProperty(value = "性别")
    @TableField("gender")
    private String gender;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;


    @ApiModelProperty(value = "修改时间",hidden = true)
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty(value = "积分余额")
    @TableField("integral")
    private BigDecimal integral;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
