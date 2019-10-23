package com.lxkj.entity;

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
 * 代理商卡片
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RetailerGiftcard对象", description = "代理商卡片")
public class RetailerGiftcard extends Model<RetailerGiftcard> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "代理商会员ID")
    @TableField("member_id")
    private String memberId;


    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;


    @ApiModelProperty(value = "卡片ID")
    @TableField("giftcard_id")
    private String giftcardId;

    @ApiModelProperty(value = "0:未使用 1:已使用")
    @TableField("state")
    private Integer state;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "0:免费(不计分销) 1:收费(计分销)")
    @TableField("lb")
    private Integer lb;

    @ApiModelProperty(value = "0:未兑换 1:已兑换")
    @TableField("`use`")
    private Integer use;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
