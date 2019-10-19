package com.lxkj.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

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
 * 安装订单
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "InstallerOrder对象", description = "安装订单")
public class InstallerOrder extends Model<InstallerOrder> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "订单ID")
    @TableField("installer_id")
    private String installerId;


    @ApiModelProperty(value = "下单会员ID")
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;


    @ApiModelProperty(value = "金额")
    @TableField("total_price")
    private BigDecimal totalPrice;


    @ApiModelProperty(value = "1 待支付，2 已支付 9支付失败")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "备注")
    @TableField("remk")
    private String remk;


    @ApiModelProperty(value = "订单号")
    @TableField("orderNo")
    private String orderNo;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "返回结果")
    @TableField("msg")
    private String msg;

    @ApiModelProperty(value = "ASMP服务订单号")
    @TableField("salesOrder")
    private String salesOrder;

    @ApiModelProperty(value = "来源订单号")
    @TableField("sourceOrderItemId")
    private String sourceOrderItemId;

   /***********************苏宁接口******************************************/
    @TableField(exist = false)
    private Order order;

    @ApiModelProperty(value = "苏宁商品编码")
    @TableField(exist = false)
    private String extdCmmdtyCtgry;

    @ApiModelProperty(value = "苏宁商品名称")
    @TableField(exist = false)
    private String extdCommodityName;

    @ApiModelProperty(value = "苏宁品牌编码")
    @TableField(exist = false)
    private String brandCode;
    @ApiModelProperty(value = "服务时间")
    @TableField(exist = false)
    private String serviceTime;

    @ApiModelProperty(value = " 国标码")
    @TableField(exist = false)
    private String code;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
