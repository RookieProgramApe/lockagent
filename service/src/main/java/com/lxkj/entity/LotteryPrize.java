package com.lxkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Zhanqian
 * @date 2019/11/11 18:03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("lottery_prize")
@ApiModel(value = "商家抽奖奖品表", description = "商家抽奖奖品表")
public class LotteryPrize extends Model<LotteryPrize> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "奖品id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "奖品类型 （1实物奖励 2虚拟奖励）")
    @TableField(value = "`type`")
    private Integer type;

    @ApiModelProperty(value = "商品id")
    @TableField(value = "cargo_id")
    private String cargoId;

    @ApiModelProperty(value = "商品名称")
    @TableField(value = "cargo_name")
    private String cargoName;

    @ApiModelProperty(value = "商品名称")
    @TableField(value = "`sort`")
    private Integer sort;

    @ApiModelProperty(value = "抽奖id")
    @TableField(value = "lottery_id")
    private String lotteryId;

    @ApiModelProperty(value = "总数量")
    @TableField(value = "`count`")
    private Integer count;

    @ApiModelProperty(value = "剩余库存")
    @TableField(value = "`num`")
    private Integer num;

    @ApiModelProperty(value = "图片")
    @TableField(value = "`img`")
    private String img;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
