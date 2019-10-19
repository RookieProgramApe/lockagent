package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 收货地址
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MemberAddress对象", description = "代理商收货地址")
public class MemberAddress extends Model<MemberAddress> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收货地址ID,空为新增/不为空为修改")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "会员ID",hidden = true)
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "收件人")
    @TableField("recipient")
    private String recipient;


    @ApiModelProperty(value = "手机号码")
    @TableField("mobile")
    private String mobile;


    @ApiModelProperty(value = "省")
    @TableField("province")
    private String province;


    @ApiModelProperty(value = "地区(市)")
    @TableField("city")
    private String city;


    @ApiModelProperty(value = "区县")
    @TableField("county")
    private String county;


    @ApiModelProperty(value = "详细地址")
    @TableField("address")
    private String address;


    @ApiModelProperty(value = "是否为默认地址  0=否 1=是")
    @TableField("`default`")
    private Boolean defaultAddress;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
