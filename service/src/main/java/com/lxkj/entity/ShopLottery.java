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
import java.util.Date;
import java.util.List;

/**
 * @author Zhanqian
 * @date 2019/11/11 13:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shop_lottery")
@ApiModel(value = "商家抽奖表", description = "商家抽奖表")
public class ShopLottery extends Model<ShopLottery> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "抽奖id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "抽奖名称")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "抽奖商家id")
    @TableField(value = "shop_id")
    private String shopId;

    @ApiModelProperty(value = "抽奖商家")
    @TableField(exist = false)
    private Shop shop;

    @ApiModelProperty(value = "抽奖商家")
    @TableField(exist = false)
    private String shopName;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "链接点击次数")
    @TableField(value = "click_count")
    private Integer clickCount;

    @ApiModelProperty(value = "参与抽奖总人数")
    @TableField(value = "partake_count")
    private Integer partakeCount;

    @ApiModelProperty(value = "中奖人数")
    @TableField(value = "win_count")
    private Integer winCount;

    @ApiModelProperty(value = "开始时间")
    @TableField(value = "start_time")
    private String startTime;

    @ApiModelProperty(value = "截至时间")
    @TableField(value = "end_time")
    private String endTime;

    @ApiModelProperty(value = "中奖概率（整数）")
    @TableField(value = "chance")
    private Integer chance;

    @ApiModelProperty(value = "是否删除(0否 1是)")
    @TableField(value = "is_del")
    private Integer isDel;

    @ApiModelProperty(value = "商品id")
    @TableField(exist = false)
    private String cargoId;

    @ApiModelProperty(value = "状态（0禁用 1正常）")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    @ApiModelProperty(value = "奖品")
    @TableField(exist = false)
    private List<LotteryPrize> prizeList = List.of();


    @ApiModelProperty(value = "中奖记录")
    @TableField(exist = false)
    private List<LotteryRecord> records = List.of();

    @ApiModelProperty(value = "当前用户本次抽奖记录")
    @TableField(exist = false)
    private List<LotteryRecord> myRecords = List.of();

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
