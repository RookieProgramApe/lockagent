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
 * 砍价订单完成情况
 * </p>
 *
 * @author 一个烧包
 * @since 2019-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BargainOrderFlow对象", description = "砍价订单完成情况")
public class BargainOrderFlow extends Model<BargainOrderFlow> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "商品ID")
    @TableField("cargo_id")
    private String cargoId;


    @ApiModelProperty(value = "砍价活动ID")
    @TableField("bargain_id")
    private String bargainId;


    @ApiModelProperty(value = "砍价订单ID")
    @TableField("bargain_order_id")
    private String bargainOrderId;


    @ApiModelProperty(value = "砍价金额")
    @TableField("price")
    private BigDecimal price;


    @ApiModelProperty(value = "序号")
    @TableField("sort")
    private Integer sort;


    @ApiModelProperty(value = "0未砍 1已砍")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "完成砍价的会员ID")
    @TableField("end_member_id")
    private String endMemberId;

    @ApiModelProperty(value = "完成砍价的会员头像")
    @TableField("end_member_id")
    private String avatar;

    @ApiModelProperty(value = "完成砍价的会员昵称")
    @TableField("end_member_id")
    private String nickname;


    @ApiModelProperty(value = "完成砍价时间")
    @TableField("end_time")
    private Date endTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
