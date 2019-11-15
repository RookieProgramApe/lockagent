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
import java.util.Date;

/**
 * @author Zhanqian
 * @date 2019/11/12 15:20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "抽奖记录表", description = "抽奖记录表")
public class LotteryRecord extends Model<LotteryRecord> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField(value = "member_id")
    private String memberId;

    @ApiModelProperty(value = "用户")
    @TableField(exist = false)
    private Member member;

    @ApiModelProperty(value = "抽奖id")
    @TableField(value = "lottery_id")
    private String lotteryId;

    @ApiModelProperty(value = "抽奖")
    @TableField(exist = false)
    private ShopLottery lottery;

    @ApiModelProperty(value = "奖品id（如果为空表示没中奖）")
    @TableField(value = "prize_id")
    private String prizeId;

    @ApiModelProperty(value = "奖品id")
    @TableField(exist = false)
    private LotteryPrize prize;

    @ApiModelProperty(value = "商家id")
    @TableField(value = "shop_id")
    private String shopId;

    @ApiModelProperty(value = "商家")
    @TableField(exist = false)
    private Shop shop;

    @ApiModelProperty(value = "创建时间（抽奖时间）")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "用户名称")
    @TableField(value = "member_name")
    private String memberName;

    @ApiModelProperty(value = "礼品名称")
    @TableField(value = "prize_name")
    private String prizeName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
