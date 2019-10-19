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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 代理商信息
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Retailer对象", description = "代理商信息")
public class Retailer extends Model<Retailer> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "代理商ID")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "领导会员ID",hidden = true)
    @TableField("parent_member_id")
    private String parentMemberId;

    @ApiModelProperty(value = "会员ID")
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "姓名")
    @TableField("name")
    private String name;


    @ApiModelProperty(value = "性别")
    @TableField("gender")
    private String gender;


    @ApiModelProperty(value = "手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "身份证号")
    @TableField("identity")
    private String identity;


    @ApiModelProperty(value = "所在地区-省",hidden = true)
    @TableField("province")
    private String province;


    @ApiModelProperty(value = "所在地区-市")
    @TableField("city")
    private String city;


    @ApiModelProperty(value = "所在地区-区县",hidden = true)
    @TableField("county")
    private String county;


    @ApiModelProperty(value = "安装电话")
    @TableField("installer_mobile")
    private String installerMobile;


    @ApiModelProperty(value = "开户人")
    @TableField("bank_holder")
    private String bankHolder;


    @ApiModelProperty(value = "开户行")
    @TableField("bank")
    private String bank;


    @ApiModelProperty(value = "银行卡号")
    @TableField("bank_account_number")
    private String bankAccountNumber;


    @ApiModelProperty(value = "0 待审核，1 终核通过  2 初审通过，99 审核不通过")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "审核意见")
    @TableField("approval_comment")
    private String approvalComment;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "卡片一级分销奖励金额",hidden = true)
    @TableField("first_award")
    private BigDecimal firstAward;


    @ApiModelProperty(value = "卡片二级分销奖励金额",hidden = true)
    @TableField("second_award")
    private BigDecimal secondAward;



    @ApiModelProperty(value = "邀请二维码")
    @TableField("qr")
    private String qr;

    @ApiModelProperty(value = "授权书")
    @TableField("authurl")
    private String authurl;

    @ApiModelProperty(value = "可提现余额")
    @TableField("balance")
    private BigDecimal balance;

    @ApiModelProperty(value = "头像")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty(value = "累计收益")
    @TableField(exist = false)
    private BigDecimal accruedIncome;

    @ApiModelProperty(value = "剩余卡片数量")
    @TableField(exist = false)
    private Integer giftcardCount;

    @ApiModelProperty(value = "直属团队数量")
    @TableField(exist = false)
    private Integer subordinateCount;


    @ApiModelProperty(value = "次属团队数量")
    @TableField(exist = false)
    private Integer dinateCount;

    
    @TableField(exist = false)
    private String remark;

    @TableField(exist = false)
    private String fileUrl;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
