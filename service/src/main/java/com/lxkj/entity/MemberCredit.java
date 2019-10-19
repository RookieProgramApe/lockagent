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
 * 积分
 * </p>
 *
 * @author 一个烧包
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MemberCredit对象", description = "积分")
public class MemberCredit extends Model<MemberCredit> {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "主键")
  @TableId(value = "id", type = IdType.UUID)
  private String id;


  @ApiModelProperty(value = "会员ID")
  @TableField("member_id")
  private String memberId;

  @ApiModelProperty(value = "1=奖励，-1=支出")
  @TableField("`type`")
  private Integer type;


  @ApiModelProperty(value = "标题")
  @TableField("title")
  private String title;


  @ApiModelProperty(value = "订单交易额")
  @TableField("`amount`")
  private BigDecimal amount;


  @ApiModelProperty(value = "获得积分数")
  @TableField("`point`")
  private BigDecimal point;


  @ApiModelProperty(value = "创建时间")
  @TableField("create_time")
  private Date createTime;

  @ApiModelProperty(value = "提货订单ID")
  @TableField("orderId")
  private String orderId;
  @Override
  protected Serializable pkVal() {
    return this.id;
  }

}
