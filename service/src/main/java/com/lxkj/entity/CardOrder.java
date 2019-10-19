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
 * 卡片订单
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CardOrder对象", description = "卡片订单")
public class CardOrder extends Model<CardOrder> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "编号")
    @TableField("sequence")
    private String sequence;


    @ApiModelProperty(value = "会员ID")
    @TableField("member_id")
    private String memberId;

    @ApiModelProperty(value = "代理商ID")
    @TableField("retailer_id")
    private String retailerId;

    @ApiModelProperty(value = "购买数量(套)")
    @TableField("count")
    private Integer count;


    @ApiModelProperty(value = "金额")
    @TableField("total_price")
    private BigDecimal totalPrice;


    @ApiModelProperty(value = "1 待审核，2 终审已通过，3 拒绝  4完成初审核")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核意见")
    @TableField("reson")
    private String reson;


    @ApiModelProperty(value = "快递公司")
    @TableField("delivery_provider")
    private String deliveryProvider;


    @ApiModelProperty(value = "运单号")
    @TableField("delivery_track")
    private String deliveryTrack;


    @ApiModelProperty(value = "收件人")
    @TableField("recipient")
    private String recipient;


    @ApiModelProperty(value = "收件人号码")
    @TableField("mobile")
    private String mobile;


    @ApiModelProperty(value = "收件地区")
    @TableField("city")
    private String city;


    @ApiModelProperty(value = "收件详细地址")
    @TableField("address")
    private String address;


    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "0自发 1代发")
    @TableField("lb")
    private Integer lb;


    @ApiModelProperty(value = "0未知  1已收费(可计分销)  2免费(不计分销)")
    @TableField("retailstate")
    private Integer retailstate;

    @TableField(exist = false)
    private String fileUrl;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
