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

/**
 * @author Zhanqian
 * @date 2019/11/11 9:25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`shop`")
@ApiModel(value = "抽奖商家", description = "抽奖商家")
public class Shop extends Model<Shop> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商家id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "会员id")
    @TableField(value = "member_id")
    private String memberId;

    @ApiModelProperty(value = "服务商id")
    @TableField(value = "retailer_id")
    private String retailerId;

    @ApiModelProperty(value = "商家名称")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "商家电话")
    @TableField(value = "phone")
    private String phone;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "分享人数")
    @TableField(value = "share_count")
    private Integer shareCount;

    @ApiModelProperty(value = "点击次数")
    @TableField(value = "click_count")
    private Integer clickCount;

    @ApiModelProperty(value = "抽奖次数")
    @TableField(value = "lottery_count")
    private Integer lotteryCount;

    @ApiModelProperty(value = "最后一次抽奖")
    @TableField(value = "last_time")
    private Date lastTime;

    @ApiModelProperty(value = "参与抽奖总人数")
    @TableField(value = "partake_count")
    private Integer partakeCount;

    @ApiModelProperty(value = "是否删除(0否 1是)")
    @TableField(value = "is_del")
    private Integer isDel;

    @ApiModelProperty(value = "状态（0禁用 1正常）")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
