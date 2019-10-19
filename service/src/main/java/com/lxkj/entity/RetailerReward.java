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

/**
 * <p>
 * 分销奖励
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RetailerReward对象", description = "代理商商品提货奖励设置")
public class RetailerReward extends Model<RetailerReward> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "会员ID")
    @TableField("member_id")
    private String memberId;



    @ApiModelProperty(value = "商品ID")
    @TableField("cargo_id")
    private String cargoId;


    @ApiModelProperty(value = "代理商信息ID")
    @TableField("retailer_id")
    private String retailerId;


    @ApiModelProperty(value = "分销奖励数值")
    @TableField("figure")
    private BigDecimal figure;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
