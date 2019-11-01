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
 * 商家的卡片订单
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CardOrders对象", description = "商家卡片订单")
public class CardOrders extends Model<CardOrders> {

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

    @ApiModelProperty(value = "商家ID")
    @TableField("retailer_id")
    private String retailerId;

    @ApiModelProperty(value = "轻奢卡购买数量(套)")
    @TableField("count_a")
    private Integer countA;

    @ApiModelProperty(value = "轻奢卡购买数量(套)")
    @TableField("count_b")
    private Integer countB;

    @ApiModelProperty(value = "轻奢卡购买数量(套)")
    @TableField("count_c")
    private Integer countC;

    @ApiModelProperty(value = "金额")
    @TableField("total_price")
    private BigDecimal totalPrice;


    @ApiModelProperty(value = "0 待审核，1终审已通过")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "审核意见")
    @TableField("reson")
    private String reson;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
