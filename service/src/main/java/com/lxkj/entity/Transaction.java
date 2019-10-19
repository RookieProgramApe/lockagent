package com.lxkj.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 支付
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Transaction对象", description = "支付")
@TableName("`transaction`")
public class Transaction extends Model<Transaction> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "会员ID",hidden = true)
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "订单ID",hidden = true)
    @TableField("order_id")
    private String orderId;


    @ApiModelProperty(value = "1 支付，80 卡片奖励，81 提货佣金，99 提现",hidden = true)
    @TableField("type")
    private Integer type;


    @ApiModelProperty(value = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "手续费")
    @TableField("commission")
    private BigDecimal commission;

    @ApiModelProperty(value = "0 审核中，1 成功，99 审核失败")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "审核意见")
    @TableField("comment")
    private String comment;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
